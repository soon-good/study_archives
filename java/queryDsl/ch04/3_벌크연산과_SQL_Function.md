# 벌크 연산과 SQL Function



# 벌크 연산

## 주의할 점

벌크연산은 메모리에 있는 영속성 객체들을 거치지 않고 바로 DB에 SQL을 보내 적용하는 기능이다. 벌크 연산을 수행 후에는 영속성 객체를 flush() 후 clear() 해줘야 한다는 점을 주의해야 한다. (벌크 연산 수행 후 flush, clear를 하지 않으면, 영속성 객체와 DB에 있는 데이터가 서로 맞지 않게 된다. 즉, JPA가 바라보는 데이터와 영속성이 잡고있는 데이터가 맞지 않는 문제.)  

(그림 추가하자)

> 벌크 연산은 영속성 컨텍스트를 무시하고 바로 DB로 직접 변경을 수행한다. 벌크 연산 후 select 쿼리 수행시 벌크연산이 적용된 값이 아닌 영속성 컨텍스트의 값이 보여진다. DB에서 select 해서 가져온 실 데이터 값이 있더라도, 이미 영속성 컨텍스트에 해당 값이 이미 있으면 DB에서 가져온 값을 버린다(무시한다).
>
> flush, clear를 해주지 않았을 경우 벌크 연산 후의 데이터 조회할 때 
>
> - DB 에는 벌크 연산 후의 값들이 적용되어 있지만
> - 영속성 컨텍스트 에는 이전 값들이 적용되어 있다.
>



## 벌크연산을 사용하는 경우

수정하려는 데이터의 양이 많을 경우  

수정하려는 1건의 데이터마다 여러 번의 쿼리 각각의 커밋을 단발성으로 수행하기보다는  

한번에 통으로 수정하는 쿼리를 만들어서 커밋을 하는 것이 효율적일 수 있다.



예를 들어, 

- 나이가 50세 이상
- 흡연자

인 보험가입자들에 대한 보험료를 일괄적으로 인상해야 하는 경우가 있다고 해보자. 해당 보험회사의 가입자는 500만 명이다. 이 경우 500만 건의 데이터를 일일이 수정해주는 것보다 한번에 통으로 업데이트를 하는 것이 나을 수도 있다.



이처럼, 벌크 연산을 하는 경우는 각각의 한 건마다 데이터를 수정하기보다는 대량으로 일괄 업데이트하는 것이 더 정확한 자료일 경우에 적합하다. 또는 batch 작업을 통한 집계성 update 작업에도 적합하다.  



## 예제 1)

24세 이하의 보험가입요청자들의 이름을 모두 미성년자로 변경하기(조금 억지스럽긴 하다.)  

```java
	@Test
	@Commit
	public void bulkUpdateTest1(){
		QMember member = QMember.member;
		System.out.println("===== result before =====");
		List<Member> resultBefore = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : resultBefore){
			System.out.println("member :: " + m);
		}

		System.out.println("");
		System.out.println("");

		System.out.println("===== bulk created... =====");
		long count = queryFactory.update(member)
			.set(member.username, "미성년자")
			.where(member.age.lt(24))
			.execute();
		System.out.println("");
		System.out.println("");

		/** flush(), clear()
		 * flush()를 해줘야 변경된 내용을 확인 가능하고
		 * clear()를 해줘야 남아있는 엔티티 중 깔끔하지 않게 남아있는 녀석들을 모두 지워준다.
		 **/

		em.flush();
		em.clear();

		assertThat(count).isEqualTo(2);

		System.out.println("===== result after =====");
		List<Member> result = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : result){
			System.out.println("member :: " + m);
		}

	}
```



## 예제 2)

나이가 50세 이상이면서 흡연자인 회원들의 보험료를 20만원 인상하기  

```java
@Test
public void testUpdatePrice(){
  QMember member = QMember.member;
  long count = queryFactory.update(member)
    .set(member.price, member.price.add(10000))
    .where(member.age.gt(50).and(member.smokerType.eq("SMOKER")))
    .fetch();
  
  em.flush();
  em.clear();
}
```



## 예제 3)

나이가 50세 이상이면서 흡연자인 회원들의 이름에 "/흡연자"라는 문자열을 붙여서 저장하기

```java
@Test
public void testUpdate50Smokers(){
  QMember member = QMember.member;
  long count = queryFacotry.update(member)
    .set(member.username, member.username.concat("/흡연자"))
    .where(member.age.gt(50).and(member.smokerType.eq("SMOKER")))
    .fetch();
  
  em.flush();
  em.clear();
}
```



# SQL Function

SQL Function은 application.yml, application.properties에 등록한 DB의 타입에 등록된 Function만 사용가능하다. 또는 사용자 정의 Function의 경우 직접 해당 DB타입Dialect 클래스를 확장한 클래스르 application.yml에 설정하여 사용가능하다.

H2를 사용할 경우에는 예로 들어보면 H2Dialect에 등록되어 있는 것만을 사용가능하다. 또는 H2Dialect를 확장(상속)하여 사용가능하다.

## 예1)

```java
	@Test
	public void testSqlFunction1(){
		QMember member = QMember.member;
		List<String> data = queryFactory
			.select(
				Expressions.stringTemplate(
					"function('replace', {0}, {1}, {2})",
					member.username, "Genie", "지니"
				)
			)
			.from(member)
			.where(member.username.eq("Genie"))
			.fetch();
	}
```

