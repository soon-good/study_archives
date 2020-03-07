package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.Tuple;
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
 * QueryDsl - Ch03-8. 집계함수와 groupBy (sum,avg,min,max,count, groupBy/having)
 * 	- 집계함수 (sum,avg,min,max,count)
 * 	- groupBy, having
 */
@SpringBootTest
@Transactional
public class QdslAggregationTest {

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

	@Test
	public void basicAggregation(){
		QMember member = QMember.member;

		/**
		 * Tuple
		 * 		데이터 타입이 여러가지로 섞여서 들어올때 사용한다. (단일 타입을 조회하는것이 아닐때)
		 */
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

	/**
	 * 팀의 이름과 각 팀의 평균 연령 구하기
	 * @throws Exception
	 */
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
}
