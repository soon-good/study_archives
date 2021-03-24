# 스프링 데이터 페이징과 QueryDsl 연동

# OVERVIEW

페이징은 페이지네이션 또는 페이징 이런 용어로 업계에서 자주 통용되는 단어이다. 보통 웹 계층 관련 로직 구현 시 페이징 기능은 꼭 거쳐야 하는 과정 중 하나이다. QueryDSL 에서는 페이징을 지원하기 위해 아래와 같은 메서드들을 제공하고 있는데, 각각의 용도들도 함께 정리해두었다.  


- **fetchResults() : QueryResults\<T\>**  
  - QueryDsl 의 페이징 기능을 사용할 때, fetch() 메서드 대신  fetchResults() 메서드를 사용한다.
  - QueryResults\<T\> 는 content, total 감싸고 있는 결과를 표현하기 위한 타입이다.
  - QueryResults\<T\> 내의 `content` 는 결과 값의 본문을 의미한다. 
  - QueryResults\<T\> 내의 `total` 은 결과 값의 본문을 의미한다.  
    
- **limit (long `limit`) : JPAQuery\<T\>** 
  - 데이터를 몇 개 단위로 묶어서 페이징할 지를 결정한다.
  - **즉, 데이터를 몇개 단위로 묶을지를 나타낸다.**
  - 0... i ... n-1의 형식이다.
  - 만약 30을 지정했다면 30단위로 결과를 나눈결과에 대한 여러개의 페이지가 생성된다.
  - JPAQuery\<T\> 를 리턴하는데, 이것은 메서드를 체이닝할 수 있도록 현재 쿼리객체를 리턴함을 의미한다.  
    
- **offset (long `offset`) : JPAQuery\<T\>**  
  - 데이터를 limit 단위로 나누었을때 만들어진 페이지들 중 몇번째(offset) 페이지를 참조할지를 결정한다.
  - **즉, 몇 번째 구간의 페이지를 보여줄지를 나타낸다. (1페이지, 2페이지 ....)**
  - 0... i ... n-1의 형식이다.
  - JPAQuery\<T\> 를 리턴하는데, 이것은 메서드를 체이닝할 수 있도록 현재 쿼리객체를 리턴함을 의미한다.



# 참고자료

- [인프런 - 실전! QueryDsl](https://www.inflearn.com/course/Querydsl-%EC%8B%A4%EC%A0%84)
- [PageRequest API - docs.spring.io/spring-data](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html)



# 예제 Repository

- [github.com/soongujung/study_archives/queryDsl/example/qdsl](https://github.com/soongujung/study_archives/tree/master/java/queryDsl/example/qdsl)



# 간단한 페이징 예제

간략한 예제를 살펴보면 아래와 같다. ([예제 소스](https://github.com/soongujung/study_archives/blob/master/java/queryDsl/example/qdsl/src/main/java/com/study/qdsl/repository/custom/MemberJpaCustomImpl.java))

**ex) searchPageSimple (MemberSearchCondition condition, Pageable pageable)**  

```java
// ...
public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable){
		QueryResults<MemberTeamDto> fetchResults = queryFactory
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
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
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchResults(); // fetchResults() 를 사용하면 content 쿼리와 count 쿼리 두번을 호출한다.

		List<MemberTeamDto> results = fetchResults.getResults();
		long total = fetchResults.getTotal();
		return new PageImpl<MemberTeamDto>(results, pageable, total);
}
```

  

**fetchResults()**  

- fetchResults() 메서드를 사용하면 querydsl 내부적으로 두번의 쿼리를 수행한다. 이 때 수행되는 쿼리는  content 쿼리, count 쿼리이다.

- querydsl로 작성된 쿼리에 orderby()가 있더라도, 카운트 쿼리시에는 무시된다.

  - 카운트 쿼리 실행시 필요없는 order by 는 제거된다.

- 카운트 쿼리는 잘못 쓰면, 실제 운영상에서 오버헤드를 야기할 수 있다. 이러한 문제로 인해 최적화하는 방법에 대해 아래의 **"최적화(1)"**에서 정리한다.

  

## 테스트 코드

```java
@SpringBootTest
@Transactional
class MemberJpaCustomTest{
  
  @Autowired
  EntityManager em;
  
  @Autowired
  MemberDataJpaRepository dataJpaRepository;
  
  @BeforeEach
  public void before(){
    Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");
		Team nullTeam = new Team("NullTeam");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);
		em.persist(nullTeam);

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

		Member ceo = new Member("Jordan", 49, null);

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
		em.persist(ceo);
  }
  
  @Test
	public void searchPageSimpleTest(){
		MemberSearchCondition condition = new MemberSearchCondition();
    
    QMember member = QMember.member;
    
		// 스프링 데이터의 페이지네이션의 page 는 0번 부터 시작된다.
		PageRequest pageRequest = PageRequest.of(0, 3);
		Page<MemberTeamDto> results = dataJpaRepository.searchPageSimple(condition, pageRequest);

		assertThat(results.getSize()).isEqualTo(3);

		assertThat(results.getContent())
			.extracting("username")
			.containsExactly("John", "Becky", "Kyle");

		System.out.println("results === ");
		System.out.println(results);
	}
}
```

  

## PageRequest.of (int page, int size)

- e.g.) PageRequest pageRequest = PageRequest.of(0, 3)
  - **3개씩 데이터의 사이즈를 구분지어 묶었을때의 첫번째 페이지의 페이지를 의미.**
  - `size` 단위로 데이터를 나누었을 때 몇번 째 페이지 ( `page` )를 들고올 것인가
  - PageRequest.of (int page, int size) 메서드를 사용했다. 
    - (참고: [PageRequest API - docs.spring.io/spring-data](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/PageRequest.html))
    - int `page`
      - page 번호를 이야기한다. 배열 인덱스를 가리킬 때 처럼 0부터 시작하는 번호를 가지게 된다. 
    - int `size`
      - size 를 의미한다. 

​    

> 참고) PageRequest 클래스  
>
> - 인터페이스 Pageable 과 호환된다.
> - PageRequest 클래스는 AbstractPageRequest 클래스를 상속한 클래스인데, AbstractPageRequest클래스는 Pageable을 implements 하고 있다.

  

# 최적화 (1) - Count시 불필요한 Join제거

현업에서 일하다보면 조회 쿼리가 복잡한 경우가 자주 있다. 이때 Content 성격의 쿼리는 굉장히 복잡하지만, Count 쿼리는 그렇게 복잡하게 Join을 하지 않고, 단일 데이터객체(테이블)의 행의 수만 들고 오면 되는 경우가 있다. (메타 정보를 가져오기 위한 left join 등의 구문으로 인해...)  

이런 경우, QueryDsl 이 반환해주는 fetchResults() 를 그대로 사용하기 보다는 fetch() 메서드로 `content` 를 가져오고, `total` 값은 count 쿼리를 새롭게 작성하여 사용하는 방식이 추천된다.

즉, 아래의 두 쿼리를 각각 따로 작성하는 방식이다.

- 데이터의 내용 ( `content` )
- 카운트 ( `total` )



**예제)** 

- 아래의 예제에서는 leftJoin 구문을 주석처리했다. 같은 데이터인데 카운트 쿼리 수행시 조인을 통해서 데이터를 조인해서 인출하는데에 발생하는 비용을 줄이기 위해서이다.  
- [예제 소스 - MemberJpaCustomImpl.java](https://github.com/soongujung/study_archives/blob/master/java/queryDsl/example/qdsl/src/main/java/com/study/qdsl/repository/custom/MemberJpaCustomImpl.java)



```java
package com.study.qdsl.repository.custom;

//...

public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

  // ...
  
	@Override
	public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {

		List<MemberTeamDto> results = queryFactory.select(
			new QMemberTeamDto(
				member.id.as("memberId"),
				member.username.as("username"),
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			)
		)
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
    
		long count = queryFactory.select(member)
			.from(member)
//			.leftJoin(member.team, team) // 필요 없을 때도 있다.
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchCount();

		return new PageImpl<MemberTeamDto>(results, pageable, count);
	}
  // ...
}
```



# 최적화 (2) - 선택적으로 count쿼리 수행

페이징 수행시 count() 쿼리를 생략해도 되는 경우

아래의 경우 count 쿼리 없이 Java 레벨에서 List 의 size() 를 통해 `total` 데이터를 구해낼 수 있는 경우이다.

- 페이지의 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
  - ex) 페이지의 사이즈는  30개로 정했는데 DB에는 20개의 글 밖에 존재하지 않을 때
- 가장 마지막 페이지의 데이터를 요청할 때
  - (offset x size) + 리스트.size() 를 통해 전체 사이즈를 구할 수 있다.
  - 즉, 카운트 쿼리 없이, 요청 정보와 결과 데이터를 적절히 조합하면 카운트 쿼리를 할 필요가 없다.

  

그리고 리턴 값은 PageableExecutionUtils::getPage() 메서드를 이용해 생성하여 리턴한다.  



예제를 보자.

- [예제 소스 - MemberJpaCustomImpl.java](https://github.com/soongujung/study_archives/blob/master/java/queryDsl/example/qdsl/src/main/java/com/study/qdsl/repository/custom/MemberJpaCustomImpl.java)

```java
package com.study.qdsl.repository.custom;

// ...

public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

	// ...
  
	@Override
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
}
```

