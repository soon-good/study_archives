package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import com.study.qdsl.entity.Team;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QueryDsl - Ch03-11. 기본 조인
 * 	- 페치 조인은 SQL에서 제공하는 것이 아닌, JPA 제공기능이다.
 * 	- SQL 조인을 활용해서 매핑되어 있는 연관된 엔티티를 SQL 한번에 조회하는 기능.
 * 	- FetchType이 Lazy인 경우 매핑 연관관계의 엔티티를 한번에 불러오지 않기 때문에 필요한 경우에 fetchJoin을 사용
 * 	- 주로 성능최적화에 사용하는 방식
 *
 *  JPA 기본편, 활용편 2탄
 * 	- JPA 기본편에서 페치조인을 어떻게 하는지 정리
 * 	- 활용편 2탄에서 자세히 다루고 있다. 페치 조인을 극한으로 몰아가서 최적화하는 것을 다뤘다.
 *
 * 	페치 조인을 테스트할 때는 영속성 컨텍스트의 값을 제때 제때 지워주지 않으면
 * 	결과를 제대로 보기 어렵다. 따라서 테스트시에 영속성 컨텍스트의 값을 지우고 시작하자.
 * 	- em.flush();
 * 	- em.clear();
 *
 * 	Member 엔티티의 @JoinColumn으로 지정해준 Team 필드는 @ManyToOne 이 걸려 있는데
 * 	 	@ManyToOne의 fetch 의 타입이 FetchType.LAZY 로 지정되어 있다.
 * 	 	FetchType 이 lazy 이기 때문에 DB에서 조회시 Member만 조회되고 Team은 조회되지 않는다.
 *
 * 매핑 연관관계 테이블 로딩 여부 확인
 * @PersistenceUnit
 * EntityManagerFactory emf;
 * 		EntityManager를 만들어내는 EntityManagerFactory 가 있는데 이 것을 @PersistenceUnit 으로 바인딩해올 수 있다.
 * 		EntityManagerFactory를 가져와서, 현재 우리의 클래스의 실행문맥(Runtime Context)에서
 * 		사용하려는 엔티티(member)에서 매핑하고 있는 참조엔티티(ManyToOne등)가 로딩된것인지 아닌지를 파악할 수 있다.
 *
 * 	자세한 예제는 아래를 참고하자.
 */

@SpringBootTest
@Transactional
public class QdslJoinFetchTest {

	@Autowired
	EntityManager em;

	@PersistenceUnit
	EntityManagerFactory emf;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

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

	/**
	 * Member 엔티티의 @JoinColumn으로 지정해준 Team 필드는 @ManyToOne 이 걸려 있는데
	 * @ManyToOne의 fetch 의 타입이 FetchType.LAZY 로 지정되어 있다.
	 *
	 * FetchType 이 lazy 이기 때문에 DB에서 조회시 Member만 조회되고 Team은 조회되지 않는다.
	 */
	@Test
	public void nonFetchJoin(){
		em.flush();
		em.clear();

		QMember member = QMember.member;
		QTeam team = QTeam.team;

		Member chopin = queryFactory.selectFrom(member)
			.where(member.username.eq("Chopin"))
			.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(chopin.getTeam());

		assertThat(loaded).as("페치(Team을 가져왔는지)되었는지 체크 - isLoaded ?? >>> ").isFalse();
	}

	/**
	 * @ManyToOne 과 같은 매핑으로 연관된 엔티티를
	 * fetchJoin()으로 가져는 예제
	 */
	@Test
	public void useFetchJoin(){
		em.flush();
		em.clear();

		QMember member = QMember.member;
		QTeam team = QTeam.team;

		Member genie = queryFactory.selectFrom(member)
			.join(member.team, team).fetchJoin()	// 이 부분이 변경되었다.
			.where(member.username.eq("Chopin"))
			.fetchOne();

		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(genie.getTeam());

		assertThat(loaded).as("페치(Team을 가져왔는지)되었는지 체크 - isLoaded ?? >>> ").isTrue();
	}
}
