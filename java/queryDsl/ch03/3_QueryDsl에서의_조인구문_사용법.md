# 3. QueryDsl에서의 조인구문 사용법

여기서는 다양한 조인구문을 java 코드로 작성하는 문법만을 다룰 예정이다.  

(**QueryDsl 조인구문과 SQL** 이라는 주제로 QueryDsl을 사용한 쿼리 사용시 실제 조인 SQL이 어떤식으로 생성되는지 따로 문서를 만들어 정리할 예정이다.)  

- 기본 조인
- on 절
- fetch 조인
  - Fetch 조인은 SQL에서 제공하는 것이 아닌 JPA 제공기능이다.
  - SQL 조인을 활용해서 매핑되어 있는 연관도니 엔티티를 SQL 한번에 조회하는 기능
  - FetchType이 Lazy인 경우 매핑 연관관계의 엔티티를 한번에 불러오지 않기 ㄸ매ㅜㄴ에 필요한 경우에 fetchJoin을 사용한다.
  - 주로 성능최적화에 사용하는 방식이다.



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



## 기본 조인 (사용법) 

우리는 관계형 데이터베이스의 SQL 사용시, 하나의 테이블과 다른 테이블을 pk로 연관지어 결과를 도출하는 것을 조인이라고 흔히 부른다. 객체지향 적으로 설계된 ORM역시 같은 개념을 사용한다. 데이터 행이 일관성을 가질 수 있도록 도울수 있는 키 값을 기준으로 서로 다른 두 테이블을 연결하는 것이 조인의 기본 컨셉인데, QueryDSL 역시 구별키 역할을 하는 필드를 매핑해 조인을 한다.  

기본적인 JOIN 구문은 아래와 같다.  

```java
		QMember member = QMember.member;
		QTeam team = QTeam.team;

		List<Member> musicianMembers = queryFactory.selectFrom(member)
			.join(member.team, team)		// Member가 mapping하는 Team과 Team엔티티를 조인한다.
      														// Join 기준은 Team.id 이며, 여기서는 묵시적으로 지정되었다.
			.where(team.name.eq("Musician"))
			.fetch();
```

- QMember member = QMember.member;  
  QTeam team = QTeam.team;
  - Q타입인 QMember 내에서 가지고 있는 member 인스턴스를 가져온다.
  - static import로 더 간결히 사용할 수도 있다
- join(member.team, team)  
  - 보통 from절 또는 where절 이후에 사용할 수 있다.
  - member 테이블과 team 테이블을 id를 기반으로 하여 조인하고 있다.
- where
  - 조인해 만들어진 결과에서 team 테이블의 name이 "Musician" 인 경우의 데이터 행들만을 추려낸다. 



## on 절



## fetch 조인



