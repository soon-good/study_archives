# 2. 검색조건,결과조회,서브쿼리,페이징,case, 상수 및 문자 처리

## Files

- QdslSearchConditionTest
- QdslSelectResultTest
- QdslSortingTest

## 목차(임시)

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

## 이부분 설명 빈약하다. 설명 추가하자!!!  

```java
QMember member = QMember.member;
List<Member> result = queryFactory.select(member)
    .from(member)
    .where(...)
    .fetch();
```

  

또는 select와 from을 합쳐서 아래와 같이 사용하기도 한다.

```java
QMember member = QMember.member;
List<Member> result = queryFactory.selectFrom(member)
    .from(member)
    .where(...)
    .fetch();
```



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
		Team musicianTeam = new Team("Musician");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		Member beethoven = new Member("Beethoven", 251, musicianTeam);
		Member chopin = new Member("Chopin", 210, musicianTeam);
		Member genie2 = new Member("Genie", 210, musicianTeam);
		Member nullName = new Member(null, 100, musicianTeam);

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);

		em.persist(beethoven);
		em.persist(chopin);
		em.persist(genie2);
		em.persist(nullName);
	}
}
```



before() 메서드에서는  

- 쉽게 설명하면, 샘플 데이터들을 영속성 영역에 저장을 해두고 있다. 

- EntityManager의 persist() 를 통해 Entity 객체들을 영속화 시킨다. (영속성 영역에 저장을 해둔다.)

- JPAQueryFactory 객체를 생성한다.

  - 현재 스프링 컨텍스트 내에 생성된 EntityManager 인스턴스를 JPAQueryFactory에 바인딩해준다.

  - JPAQueryFactory 인스턴스 입장에서는 인스턴스 생성시 JPAQueryFactory 인스턴스가 바라봐야하는  EntityManager 인스턴스가 어떤 것인지 알려주어야 한다.

  - 이와 같은 역할을 해주는 구문이

    - queryFactory = new JPAQueryFactory(em);  

      이다.



## 1) 검색조건 적용 - where 구문 

조회 구문에 검색 조건을 적용하여 검색 결과를 추려낼 때에는 

- and(), or() 사용해 조건을 연결한다.
- and 조건일 경우에 한해, BooleanExpression을 반환하는 여러개의 메서드를 나열한다.
  - 참고자료) 가변인자에 대해...
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

일단 이 문서의 목적은 공부가 목적이 아닌, 실무를 목적으로 실용적인 예를 정리하는 것이 목적이므로 원리적인 이야기는 일단 건너 뛰고 fetch(), fetchCount(), fetchFirst(), fetchResult(), fetchCount() 의 쓰임새를 파악하도록 하자.  

select() 로 얻어온 JPAQuery\<T\> 를 from(), where(), join()을 계속해서 이어붙이면서 from(), where(), join() 의 결과로 JPAQuery\<T\>를 리턴하게 된다. 예를 들어보면 아래와 같다.  

```java
JPAQuery<Member> queryContext = 
  	queryFactory
  		.select(...)			// 얻어온 Entity 들 중에서 어떤 컬럼(필드)을 보일지 기술
  		.from(...)				// Entity 에서 얻어온다.
  		.where(...)				// 어떤 Entity에서 데이터를 가져올지 기술
  		.join(...)				// join을 어떤 Entity와 할지 기술
```

  

이렇게 얻어온 JPAQuery\<T\> 인스턴스로는 fetchXXX()를 호출할 수 있다. 

```java
List<Member> results = queryContext.fetch();
```

  

이렇게 fetchXXX() 함수로 보통 SQL 동작을 마무리 짓는다. Java의 Stream API 사용시 마지막에 collect() 함수를 호출하는 것과 유사한 모양이다.  

또는 모두 이어 붙여서 한번에 호출하기도 한다.  

```java
List<Member> results = 
  	queryFactory
  		.select(...)			// 얻어온 Entity 들 중에서 어떤 컬럼(필드)을 보일지 기술
  		.from(...)				// Entity 에서 얻어온다.
  		.where(...)				// 어떤 Entity에서 데이터를 가져올지 기술
  		.join(...)				// join을 어떤 Entity와 할지 기술
  		.fetch();					// 동적 생성된 SQL 구문 실행(Transaction 또는 영속성 연산 수행)
```

  

```java
List<Member> results = queryContext.fetch();
```

위의 구문에서 queryContext 라는 이름의 JPAQuery\<T\> 타입의 인스턴스로 

- fetch(), 
- fetchCount(), 
- fetchFirst(), 
- fetchResult(), 
- fetchCount() 

를 호출할 수 있다. 그런데, 실제로 JPAQuery 클래스 내에는 fetch(), fetchCount(), fetchFirst(), fetchResult(), fetchCount() 를 멤버 메서드로 기술하고 있지 않다. 그럼 이 메서드들은 어디에 있는 것일까?  

JPAQuery 클래스의 부모인  

>  AbstractJPAQuery 클래스

에서 fetch(), fetchCount(), fetchFirst(), fetchResult(), fetchCount() 를 구현하고 있다. AbstractJPAQuery에서는 주로 fetch(), fetchCount(), ...  getResultList(), getSingleResult() 등의 데이터를 반환하는 역할의 함수들이 기술되어 있다.  

  

즉, JPAQuery 클래스는

- AbstractJPAQuery\<T, JPAQuery\<T\>\> 클래스

를 부모클래스로 두고 있다(AbstractJPAQuery 클래스를 확장하고 있다). 그리고 AbstractJPAQuery 클래스 내에 fetchXXX()에 대한 동작을 기술하고 있다. 클래스의 설계를 보는 것이 목적은 아니기 때문에 원리를 파악하는 것은 여기 까지만 하고, 우리는 일단, 실무에서 대용량의 데이터를 QueryDsl을 통해 직접 실제 SQL처럼 사용하는 것처럼 적용하는 것이 목표이다.  

따라서, fetch(), fetchCount(), fetchFirst(), fetchResult(), fetchCount() 의 용도만 간략히 파악하고, 예제만 짚어보고 넘어가자!!  

  

### fetch()

fetch()는 List\<T\> 와 같은 리스트 타입의 선형적인 자료구조를 얻어낼 때 쓴다. SQL과 비교해보면  

```sql
Select * From Members;
-- limit, offset 등을 기술하지 않은 것을 유심히 보자.
```

과 같다. 조건에 일치하는 데이터를 모두 가져올 때 사용한다.  

내부적인 호출을 아주 쬐끔만 감만 잡을 정도로만 보자...  

fetch() 메서드는 AbstractJPAQuery 클래스 내에 아래와 같이 기술되어 있다.  

```java
// ...
		@Override
    @SuppressWarnings("unchecked")
    public List<T> fetch() {
        try {
            Query query = createQuery();	// 이 부분 흥미롭지 않은가? 나만 그럴수도 있다.
            return (List<T>) getResultList(query);
        } finally {
            reset();
        }
    }
// ...
```

select(...)가 반환하는 JPAQuery 객체를 통해 from(...).where(...).join(...) 을 호출한 결과를 List\<T\> 타입으로 뽑아내주고 있다. createQuery() 메서드는 두번에 걸쳐서 감싸서 기술되어 있는데 최종적으로 들르는 구문은 아래와 같다.  

```java
// ...
// ...
		// 생성자를 통한 의존성 주입
		// EntityManager를 주입받고 있다.
    public AbstractJPAQuery(EntityManager em, JPQLTemplates templates, QueryMetadata metadata) {
        super(metadata, templates);
        this.queryHandler = templates.getQueryHandler();
        this.entityManager = em;
    }

// ...
// ...

		private Query createQuery(@Nullable QueryModifiers modifiers, boolean forCount) {
        JPQLSerializer serializer = serialize(forCount);
        String queryString = serializer.toString();
        logQuery(queryString, serializer.getConstantToLabel());
      
      	// 의존성을 통해 주입받은 entityManager를 통해 최종적으로 createQuery 하고 있다.
        Query query = entityManager.createQuery(queryString);
      
        JPAUtil.setConstants(query, serializer.getConstantToLabel(), getMetadata().getParams());
        if (modifiers != null && modifiers.isRestricting()) {
            Integer limit = modifiers.getLimitAsInteger();
            Integer offset = modifiers.getOffsetAsInteger();
            if (limit != null) {
                query.setMaxResults(limit);
            }
            if (offset != null) {
                query.setFirstResult(offset);
            }
        }
				// ... 중략 ...
      	// ... 중략 ...
        return query;
    }
// ... 
```

  

### fetchCount()

fetchCount()를 사용하는 예제이다. 가져온 결과 행의 수를 파악하는 데에 사용된다.  

```java
	@Test
	public void fetchCount(){
		final QMember member = QMember.member;
		long count = queryFactory.selectFrom(member).fetchCount();
	}
```

  

### fetchFirst()

fetchFirst()를 사용하는 예제이다. 가져온 결과의 가장 첫 번째 행을 얻는다.  

```java
	@Test
	public void fetchFirst(){
		final QMember member = QMember.member;
		Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
	}
```

  

### fetchResults()

fetchResults() 메서드는 페이징을 포함하는 쿼리를 수행한다.  

fetchResults() 메서드의 경우 페이징 쿼리가 복잡해지면,

- 데이터(컨텐츠)
- 카운트

를 가져오는 쿼리의 값이 다를 때가 있다. (성능 때문에..)  

성능 문제로 인해 카운트를 가져오는 쿼리를 더 단순하게 만드는 경우가 있다. 복잡하고 성능이 중요한 쿼리에서는 fetchResults()로 한번에 작성하기 보다는 쿼리 두번을 보내는 식으로 작성하는 편이 낫다. 조심하자. 이 부분에 대해서는 6장 쯤에 성능 최적화를 위한 예제를 다루면서 정리를 하고 있다.     

```java
	@Test
	public void fetchResults(){
		final QMember member = QMember.member;
		QueryResults<Member> qResult = queryFactory.selectFrom(member)
														.fetchResults();

		long total = qResult.getTotal();
		List<Member> results = qResult.getResults();

		System.out.println("total cnt 	:: " + total);
		System.out.println("results 	:: " + results);
	}
```



### fetchCount()

count() 만 수행한다.  

예제)

```java
	@Test
	public void fetchCount(){
		final QMember member = QMember.member;
		long count = queryFactory.selectFrom(member).fetchCount();
	}
```



## 3) 정렬

QueryDsl로 뽑아내려는 SQL에 정렬을 적용하고 싶을때 orderBy()를 사용하면 된다. 실제 SQL을 연상할 수 있도록 메서드 이름도 잘지어져 있다!! 우리는 SQL을 작성할 때 보통 order by를 사용해야 할때 오름차순으로 할지, 내림차순으로 할지 등을 지정했었다. QueryDsl 역시 해당 기준을 제공해준다.

- asc()
  - 오름차순 정렬
- desc()
  - 내림차순 정렬
- nullsLast()
  - 지정한 컬럼의 데이터 값이 null이 아닌 데이터를 우선으로 정렬
- nullsFirst()
  - 지정한 컬럼의 데이터 값이 null인 데이터를 우선으로 정렬

asc(), desc(), nullsLast(), nullsFirst() 를 활용해 정렬구문을 작성해보자.  

  

**예제 1) 모든 회원들을 조회하는데 나이순으로 내림차순(desc), 이름순으로 오름차순(asc), 회원 이름이 없을 경우는 마지막에 출력하도록 한다. (nulls last)**  

```java
// ... 
	@Test
	public void sort(){
		final QMember member = QMember.member;

		List<Member> sortResult = queryFactory
			.selectFrom(member)
			.where(member.age.goe(100))
			.orderBy(
				member.age.desc(),
				member.username.asc()
					.nullsLast()
			)
			.fetch();

		/** 예상 결과)
		 * 		베토벤 -> 쇼팽 -> 지니 -> null
		 **/
		Member beethoven = sortResult.get(0);
		Member chopin = sortResult.get(1);
		Member genie = sortResult.get(2);
		Member nullEntity = sortResult.get(3);

		assertThat(beethoven.getUsername()).isEqualTo("Beethoven");
		assertThat(chopin.getUsername()).isEqualTo("Chopin");
		assertThat(genie.getUsername()).isEqualTo("Genie");
		assertThat(nullEntity.getUsername()).isNull();
	}
// ...
```



## 4) 페이징(기본적인 기능만)

전자정부 프레임워크 또는 마이바티스 사용시 우리는 보통 페이지네이션을 접했다. 비록 java 개발이 아니더라도 django와 같은 유명한 웹 프레임워크를 사용할 때에도 페이지네이션을 사용한다. 페이징 기능 개발은 개발자의 숙명이다.  

QueryDsl을 사용하면, QueryDsl에서 기본적으로 제공하는 fetchResults(), limit(), offset() 등을 이용하여 페이지네이션을 구현 가능하다.  

- fetchResults()
- limit()
- offset()

fetchResults(), limit(), offset()을 활용해 기본적인 페이지네이션을 구현할 수 있다. 하지만, 데이터가 많을 경우에는 기본으로 제공되는 fetchResults() 함수 하나만을 사용하는 것은 추천하지 않는다. 단지 디폴트 옵션일 뿐이다. 서버나 이런 것들을 설치할 때도 디폴트 옵션이 있지 않은가? 그런 것이라고 이해만 해두자.  

**예) 아주 기본적인 예**

```java
	@Test
	public void paging2(){
		QMember member = QMember.member;
		QueryResults<Member> queryResults = queryFactory.selectFrom(member)
			.orderBy(member.age.desc())
			.offset(1)
			.limit(2)
			.fetchResults();

		assertThat(queryResults.getTotal()).isEqualTo(10);
		assertThat(queryResults.getLimit()).isEqualTo(2);
		assertThat(queryResults.getOffset()).isEqualTo(1);
		assertThat(queryResults.getResults().size()).isEqualTo(2);
	}
```



항상 그렇듯 기본으로 제공되는 것에 너무 과한 기대를 하지 말자. 공짜로 얻는 물건에 너무 과한 기대를 하는 것은 가끔 양심에 찔릴때가 있다. QueryDsl 역시 실무에서는 기본적으로 제공되는 페이지네이션보다는 커스터마이징을 해서 사용하는 경우가 많다.  

대용량의 테이블을 조회할 때 한번에 너무 많은 양을 한번에 들고 오거나, 또는 사이즈가 큰 테이블에 대해 카운트 쿼리와 같은 Aggregation 계열의 함수를 사용할 경우 성능상에 이슈가 된다. 페이징에 대해서는 따로 문서를 정리해둘 생각이다. 지금은 아래의 단순한 예제를 잠깐이나마 보고 그냥 넘어가자.  

**예) 웹 계층에서 파라미터를 받아 직접 QueryDsl을 호출하는 예제**     

```java
	public Page<MemberTeamDto> searchPageOptimized(MemberSearchCondition condition, Pageable pageable) {
		List<MemberTeamDto> results = queryFactory.select(new QMemberTeamDto(
			member.id.as("memberId"),
			member.username.as("username"),
			member.age,
			member.team.id.as("teamId"),
			member.team.name.as("teamName")
		))
		.from(member)
		.leftJoin(member.team, team)
		.where(
			userNameEq(condition),
			teamNameEq(condition),
			ageGoe(condition),
			ageLoe(condition)
		)
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();

		/** Query 를 람다 표현식에 저장 */
		JPAQuery<Member> countSql = queryFactory
			.select(member)
			.from(member)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			);

		// SQL을 람다 표현식으로 감싸서 람다 또는 메서드 레퍼린스를 인자로 전달해준다.
		// PageableExecutionUtils 에서 위의 1),2) 에 해당하면 SQL 호출을 따로 하지 않는다.
//		return PageableExecutionUtils.getPage(results, pageable, ()->countSql.fetchCount());
		// or
		return PageableExecutionUtils.getPage(results, pageable, countSql::fetchCount);
	}
```

  

## 5) Aggregation (그루핑, 집합)

최소, 최대, 평균, sum 값을 구하는 방법을 알아보자. 최소, 최대, 평균, sum 값을 구하기 위해서는 유일하게 식별할 수 있는 기준(주로 pk 컬럼)으로 그루핑을 한 후 최소, 최대, 평균, sum을 구한다. 이렇게 집계를 내리기 위해 그루핑을 하는 것을 Aggregation 이라고 부른다.  

실무에서는 보통 데이터의 양이 많아질 경우 Web 계층에서 WAS로 조회요청을 할 때 count, min, max, sum을 할때 성능상에 무리가 가는 경우가 많다. 많은 데이터에 대해 count, min, max, sum을 단 한번의 조회요청에 수행하게 될 뿐만 아니라, 여러 사용자가 이런 집계연산을 내리는 경우 DB에 부하가 많이 가게 된다. 이런 이유로 실무에서는 여러가지 방법을 고안해낸다.  

여러가지 방법이 있겠지만, 아직까지 내가 경험해본 바로는 집계 테이블을 따로 설계한 후 batch 프로그램을 통해 직접 최소/최대/평균/sum 값을 직접 관리했었다. count 의 경우는 가장 최근의 데이터에 +1 하는 방식으로 누적했던 것으로 기억한다. 아무튼... 테이블 설계를 따로 두어 프로젝트를 진행한다고 해도, 배치 프로그램 작성 역시 우리의 몫으로 돌아오는 것은 맞다. 배치 프로그램 작성시에도 QueryDsl을 사용하면 좋겠지.  

예제를 살펴보자!!!

**예) 회원의 수, 회원들중 최고령자, 최소연령인 회원, 회원들의 평균 나이, 회원들의 나이의 총합을 구해보자**

```java
	@Test
	public void basicAggregation(){
		QMember member = QMember.member;
    
		List<Tuple> result = queryFactory
			.select(
				member.count(),
				member.age.max(),
				member.age.min(),
				member.age.avg(),
				member.age.sum()
			).from(member)
			.fetch();

		int expectedSum = 23+22+28+24+35+41+251+210+210+100;
		Double expectedAvg = expectedSum / 10.000;

		int expectedMin = 22;
		int expectedMax = 251;

		Tuple tuple = result.get(0);
		assertThat(tuple.get(member.age.max())).isEqualTo(expectedMax);
		assertThat(tuple.get(member.age.min())).isEqualTo(expectedMin);
		assertThat(tuple.get(member.count())).isEqualTo(10);
		assertThat(tuple.get(member.age.sum())).isEqualTo(expectedSum);
		assertThat(tuple.get(member.age.avg())).isEqualTo(expectedAvg);
	}
```

각 QType 엔티티의 필드에

- .max()
- .min()
- .avg()
- .sum()
- .count() 

를 수행했다. 또 다른 예제를 살펴보자  

**예제) 팀의 이름과 각 팀의 평균 연령 구하기**

```java
	@Test
	public void groupBy() throws Exception{
		QTeam team = QTeam.team;
		QMember member = QMember.member;

		List<Tuple> result = queryFactory
			.select(team.name, member.age.avg())
			.from(member)
			.join(member.team, team)
			.groupBy(team.name)
			.fetch();

		Tuple analysis = result.get(0);
		Tuple marketing = result.get(1);
		Tuple musician = result.get(2);

		for(Tuple t : result){
			System.out.println("t : " + t);
		}

		assertThat(analysis.get(team.name)).isEqualTo("Analysis");
		assertThat(analysis.get(member.age.avg())).isEqualTo(32.0);

		assertThat(marketing.get(team.name)).isEqualTo("Marketing");
		assertThat(marketing.get(member.age.avg())).isEqualTo(22.5);

		assertThat(musician.get(team.name)).isEqualTo("Musician");
		assertThat(musician.get(member.age.avg())).isEqualTo(192.75);
	}
```



## 6) case



## 7) 상수, 문자 처리

