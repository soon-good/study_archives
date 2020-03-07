package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QueryDsl - Ch03-9. 기본 조인
 *  - 기본조인, leftJoin, crossJoin을 다룬다.
 *  - 이 역시 실무에서 사용하려면 SQL 을 잘 알아야 하고,
 *  - 변환된 SQL도 논리적으로 파악해야 한다.
 */
@SpringBootTest
@Transactional
public class QdslJoinBasicTest {

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

	@Test
	public void basicJoin(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;

		List<Member> musicianMembers = queryFactory.selectFrom(member)
			.join(member.team, team)
			.where(member.team.name.eq("Musician"))
			.fetch();

		for(Member m : musicianMembers){
			System.out.println("m : "+ m);
		}

		assertThat(musicianMembers)
			.extracting("username")
			.containsExactly("Beethoven", "Chopin", "Genie", null);

		/**
			 select
				 member0_.member_id as member_i1_1_,
				 member0_.age as age2_1_,
				 member0_.team_id as team_id4_1_,
				 member0_.username as username3_1_
			 from
				 member member0_
			 inner join
				 team team1_
					 on member0_.team_id=team1_.id
			 where
			 	team1_.name=?
		 */
	}

	@Test
	public void leftJoin(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;

		List<Member> analysisMembers = queryFactory.selectFrom(member)
			.leftJoin(member.team, team)
			.where(member.team.name.eq("Analysis"))
			.fetch();

		assertThat(analysisMembers)
			.extracting("username")
			.containsExactly("Kyle","Stacey","Aladdin","Genie");

		/**
			select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_
			left outer join
				team team1_
					on member0_.team_id=team1_.id
			where
				team1_.name=?
		 */
	}

	/**
	 * Team 이름이 null 인경우를 가정해 그냥 테스트 해봄.
	 * Team Type을 enum으로 지정해도 좋지 않을까?
	 */
	@Test
	public void nullTeamTest(){
		final QMember member = QMember.member;
		final QTeam team = QTeam.team;

		List<Member> jordanTeam = queryFactory.selectFrom(member)
			.leftJoin(member.team, team)
			.where(member.username.eq("Jordan"))
			.fetch();

		for(Member m : jordanTeam){
			System.out.println("m : " + m);
		}

		/**
			select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_
			left outer join
				team team1_
					on member0_.team_id=team1_.id
			where
				member0_.username=?

		 		m : Member(id=15, username=Jordan, age=49)
		 */
	}

	/**
	 * 세타 조인
	 *  : 연관관계가 없어도 조인을 못하나?
	 *  - 연관관계가 없어도 조인을 할 수 있다.
	 *  : from 절에서 여러 엔티티를 선택해 조인이 가능하다.
	 *  : 단 이 경우 외부조인이 불가능하다.
	 *  : 이런 연관관계 없는 테이블도 on절을 사용해 외부조인이 되도록 hibernate 최신(비교적 최신, 몇년전쯤)에 on()이 추가되었다.
	 */
	@Test
	public void thetaJoin(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		
		/**
		 * 모든 회원을 다 가져오고
		 * 모든 팀을 다 가져온 후에
		 *
		 * 이 둘을 마구잡이로 모두 조인하는 것
		 *
		 * 이후에 where 절에서 이것을 필터링 한다.
		 * DB가 최적화를 다 한다고는 하는데, 글쎄... 나도 잘 모르겠다 이건.
		 */
		em.persist(new Member("Musician"));
		em.persist(new Member("Analysis"));
		em.persist(new Member("Marketing"));

		List<Member> weirdData = queryFactory.select(member)
			.from(member, team)
			.where(member.username.eq(team.name))
			.fetch();

		System.out.println(weirdData);

		/**
			select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_ cross
			join
				team team1_
			where
				member0_.username=team1_.name
		*/
	}
}
