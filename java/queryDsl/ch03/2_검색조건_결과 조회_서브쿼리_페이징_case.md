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

에서 fetch(), fetchCount(), fetchFirst(), fetchResult(), fetchCount() 를 구현하고 있다.  

  

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

orderBy()  



## 4) 페이징

  

## 5) Aggregation (그루핑, 집합)



## 6) case



## 7) 상수, 문자 처리

