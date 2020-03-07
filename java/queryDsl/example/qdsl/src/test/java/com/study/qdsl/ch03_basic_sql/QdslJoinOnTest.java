package com.study.qdsl.ch03_basic_sql;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

/**
 * QueryDsl - Ch03-10. 기본 조인
 *  on 절을 지원하기 시작한 것은 JPA 2.1 부터 지원하기 시작했다.
 *  on 절은 주로 아래와 같은 경우에 사용된다.
 *  	- 조인 대상을 필터링 해야 할때 (조인 하기전에 필터링해서 가져오는 경우)
 *  	- 연관관계 없는 엔티티 간에 외부조인을 할 때
 *
 *  basicOn(), basicOn2(), basicOn3()
 *
 *  on 절을 활용한 조인 대상 필터링을 사용할 때
 *   - inner join 일 경우 where 절로 충분히 해결된다.
 *   - 외부조인이 필요한 경우 on() 을 사용한다.
 */
@SpringBootTest
@Transactional
@Commit
public class QdslJoinOnTest {

	@Autowired
	EntityManager em;

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
	 * ex 1) 회원과 팀을 조인한다. 팀 이름이 "Musician"인 팀만 조인하고, 회원은 모두 조회하자.
	 * 		조인 대상을 필터링 해야 할때 (조인 하기전에 필터링해서 가져오는 경우)
	 *
	 * 강좌에서는 leftJoin()을 사용했는데, 잘못됐다. join() 또는 innerJoin()을 사용했어야 한다.
	 * 실제 SQL을 파악하기 위한 좋은 예제가 되었다.
	 */
	@Test
	public void basicOn1(){
		final QMember member = QMember.member;
		final QTeam team = QTeam.team;

		List<Tuple> musicians = queryFactory.select(member, team)
			.from(member)
			.leftJoin(member.team, team)
			.on(team.name.eq("Musician"))
			.fetch();

		for(Tuple t : musicians){
			System.out.println("team :: " + t);
		}

		/**
			select
				member0_.member_id as member_i1_1_0_,
				team1_.id as id1_2_1_,
				member0_.age as age2_1_0_,
				member0_.team_id as team_id4_1_0_,
				member0_.username as username3_1_0_,
				team1_.name as name2_2_1_
			from
				member member0_
			left outer join
				team team1_
					on member0_.team_id=team1_.id
					and (
						team1_.name=?
					)

		 team :: [Member(id=5, username=John, age=23), null]
		 team :: [Member(id=6, username=Becky, age=22), null]
		 team :: [Member(id=7, username=Kyle, age=28), null]
		 team :: [Member(id=8, username=Stacey, age=24), null]
		 team :: [Member(id=9, username=Aladdin, age=35), null]
		 team :: [Member(id=10, username=Genie, age=41), null]
		 team :: [Member(id=11, username=Beethoven, age=251), Team(id=3)]
		 team :: [Member(id=12, username=Chopin, age=210), Team(id=3)]
		 team :: [Member(id=13, username=Genie, age=210), Team(id=3)]
		 team :: [Member(id=14, username=null, age=100), Team(id=3)]
		 team :: [Member(id=15, username=Jordan, age=49), null]
		*/
	}

	@Test
	public void basicOn2() throws Exception{
		final QMember member = QMember.member;
		final QTeam team = QTeam.team;

		List<Tuple> musicians = queryFactory.select(member, team)
			.from(member)
			.join(member.team, team)
			.on(team.name.eq("Musician"))
			.fetch();

		for(Tuple t : musicians){
			System.out.println("team :: " + t);
		}

		/**
			select
				member0_.member_id as member_i1_1_0_,
				team1_.id as id1_2_1_,
				member0_.age as age2_1_0_,
				member0_.team_id as team_id4_1_0_,
				member0_.username as username3_1_0_,
				team1_.name as name2_2_1_
			from
				member member0_
			inner join
				team team1_
					on member0_.team_id=team1_.id
					and (
						team1_.name=?
					)


			team :: [Member(id=11, username=Beethoven, age=251), Team(id=3)]
			team :: [Member(id=12, username=Chopin, age=210), Team(id=3)]
			team :: [Member(id=13, username=Genie, age=210), Team(id=3)]
			team :: [Member(id=14, username=null, age=100), Team(id=3)]
		*/
	}

	/**
	 * 위의 basicOn1()의 queryDSL은 아래와 같이 바꿔서 표현가능하다... 라고 하지만,
	 * 데이터의 결과가 다르게 나왔다 ㅋㅋㅋ
	 *
	 * 실제로는 basicOn2()의 결과가 basicOn3()의 결과와 같다.
	 * 	- leftJoin()이 아니라 join()을 사용할때 on()절을 사용하는 것의 차이점을 설명하시려 한건데,
	 * 	- 강의 중이라 정신이 없어서 실수하신듯...
	 *
	 * 역시 SQL 은 직접 확인해서 해봐야 할 듯하다.
	 */
	@Test
	public void basicOn3(){
		final QMember member = QMember.member;
		final QTeam team = QTeam.team;

		List<Tuple> musicians = queryFactory
			.select(member, team)
			.from(member)
			.join(member.team, team)
			.where(team.name.eq("Musician"))
			.fetch();

		for(Tuple t : musicians){
			System.out.println("team :: " + t);
		}

		/**
			select
				member0_.member_id as member_i1_1_0_,
				team1_.id as id1_2_1_,
				member0_.age as age2_1_0_,
				member0_.team_id as team_id4_1_0_,
				member0_.username as username3_1_0_,
				team1_.name as name2_2_1_
			from
				member member0_
			inner join
				team team1_
					on member0_.team_id=team1_.id
			where
				team1_.name=?

			team :: [Member(id=11, username=Beethoven, age=251), Team(id=3)]
			team :: [Member(id=12, username=Chopin, age=210), Team(id=3)]
			team :: [Member(id=13, username=Genie, age=210), Team(id=3)]
			team :: [Member(id=14, username=null, age=100), Team(id=3)]
		*/
	}
}
