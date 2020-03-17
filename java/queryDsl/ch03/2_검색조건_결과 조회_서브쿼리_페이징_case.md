# 2. 검색조건,결과조회,서브쿼리,페이징,case, 상수 및 문자 처리



- 검색조건 where를 사용하는 다양한 방식
- 결과조회
  - fetch()
  - fetchOne()
  - fetchFirst()
  - fetchResults()
  - fetchCount()
- 정렬
  - orderBy()
- 페이징
  - limit(), offset()
- 집합(aggregation)
- case
- 상수 /문자 처리



QueryDsl을 이용한 조회 구문 작성시 여러개의 리스트를 조회시의 가장 일반적인 구조는 아래와 같다.

> ```java
> QMember member = QMember.member;
> List<Member> result = queryFactory.select(member)
>   .from(member)
>   .where(...)
>   .fetch();
> ```



## 예제 데이터 만들기

JUnit 테스트를 작성해보자. 

- 샘플 데이터를 insert하는 메서드인 before() 메서드를 작성하고, 
- @BeforeEach 애노테이션을 적용해주어 
- Unit test를 시작하기 전에 @BeforeEach가 적용된 메서드가 실행될 수 있도록 해주자.  



```java
package com.study.qdsl.ch03_basic_sql;
// ...
// ...
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
@Transactional
public class QdslSearchCondtionTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");

		em.persist(marketingTeam);
		em.persist(analysisTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);
	}
}
```



before() 메서드에서는  

- 쉽게 설명하면, 샘플 데이터들을 영속성 영역에 저장을 해두고 있다. 

- EntityManager의 persist() 를 통해 Entity 객체들을 영속화 시킨다. (영속성 영역에 저장을 해둔다.)

- JQueryFactory 객체를 생성한다.

  - 현재 스프링 컨텍스트 내에 생성된 EntityManager 인스턴스를 JQueryFactory에 바인딩해준다.

  - JQueryFactory 인스턴스 입장에서는 인스턴스 생성시 JQueryFactory 인스턴스가 바라봐야하는  EntityManager 인스턴스가 어떤 것인지 알려주어야 한다.

  - 이와 같은 역할을 해주는 구문이

    - queryFactory = new JPAQueryFactory(em);  

      이다.



## 1) 검색조건 적용 - where 구문 

조회 구문에 검색 조건을 적용하여 검색 결과를 추려낼 때에는 

- and(), or() 사용해 조건을 연결한다.
- and 조건일 경우에 한해, BooleanExpression을 반환하는 여러개의 메서드를 나열한다.
  - 참고자료)
    - [java의 가변인자 Varargs](https://gyrfalcon.tistory.com/entry/Java-Varargs)

와 같은 두가지의 방식을 사용하는 편이다.  

  

- **and(), or()를 활용해 조건식을 메서드 체이닝으로 연결하는 방식**  
  - 메서드 체이닝을 이용해 들여쓰기로 작성이 가능하다.
  - 해당 비교 로직이 비즈니스로직 관점에서 제품의 특징?같은 것이 확정되지 않았을 경우 주로 사용하는 방식이다.
  - and(), or() 비교조건은 개발시 공통성격을 지니는 비교 구문이 생길 수 있다. 
  - 이 경우 아래와 같이 BooleanExpression을 반환하는 메서드로 분리해 메서드를 따로 두어 기능을 고정시킬 수 있다.        
- **BooleanExpression을 반환하는 메서드를 여러개 나열(,로 구분 - 가변인자 개념(Varargs)을 활용)**  
  - 이 방식은 코드가 길어지므로 구체적인 방법은 추후 새로 문서를 작성해 정리할 예정이다.
  - BooleanExpression을 사용하는 메서드를 이용할 경우 유지보수가 편해진다.
  - 동적 쿼리 작성시, 이렇게 파라미터로 보내는 방식을 사용할 경우 굉장히 편리해진다.
  - 중복되는 같은 로직이 여러곳에 있기보다 중복되는 로직은 하나의 공통로직에 두거나 객체지향적으로 구성할 수 있게 되기 때문에 유지 보수 관점에서 조금 더 용이하다.

하는 방식으로 검색 조건을 적용할 수 있다.  

  

**예제) 멤버들 중 이름이 Aladdin 이면서, 나이가 30~40인 멤버 찾기**  

and(), or()를 활용해 조건식을 메서드 체이닝으로 연결하는 방식  

```java
	// ...
	@Test
	public void search(){
		QMember member = QMember.member;

		Member selectedMember = queryFactory.select(member)
			.from(member)
			.where(
				member.username.eq("Aladdin")
					.and(member.age.between(30, 40))
			)
			.fetchOne();

		assertThat(selectedMember.getUsername()).isEqualTo("Aladdin");
	}
	// ...
```

  

**예제) 멤버들 중 이름이 Aladdin 이면서, 나이가 35 인 멤버 찾기**  

BooleanExpression을 반환하는 함수를 여러개 나열하는 방식  

```java
	// ...
	@Test
	public void searchAndParam(){
		final QMember member = QMember.member;

		Member memberResult = queryFactory.selectFrom(member)
			.where(
				member.username.eq("Aladdin"),
				member.age.eq(35)
			)
			.fetchOne();

		assertThat(memberResult.getUsername()).isEqualTo("Aladdin");
	}
	// ...
```



## 2) 결과조회

결과조회

- fetch()
- fetchOne()
- fetchFirst()
- fetchResults()
- fetchCount()



## 3) 정렬

orderBy()  



## 4) 페이징

  

## 5) Aggregation (그루핑, 집합)



## 6) case



## 7) 상수, 문자 처리

