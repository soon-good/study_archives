package com.study.qdsl.advanced.ch04.projections.item01;

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

/**
 *
 * Ch03 - item1
 * 프로젝션
 * 	: select 절에 무엇을 가져올지 지정하는 것
 *  : 프로젝션 할 컬럼이 하나이면 타입을 명확하게 지정할 수 있다. (ex. username만 가져오는 경우)
 *  : 프로젝션 할 컬럼이 여러개이면 튜플 or DTO 로 조회가능하다.
 */
@SpringBootTest
@Transactional
public class QdslBasicTupleTest {

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
	 * 프로젝션 대상이 하나의 컬럼인 경우
	 * : 간단히 해결된다.
	 */
	@Test
	public void simpleProjection(){
		QMember member = QMember.member;

		List<String> simpleData = queryFactory.select(member.username)
			.from(member)
			.fetch();

		System.out.println(simpleData);
	}

	/**
	 * 프로젝션 대상이 여러컬럼일 경우
	 * : DTO를 쓰거나, Tuple을 쓴다.
	 * : 아래 예제는 tuple을 사용
	 */
	@Test
	public void tupleProjection(){
		QMember member = QMember.member;

		List<Tuple> sample = queryFactory
			.select(member.age, member.username, member.team)
			.from(member)
			.fetch();

		for(Tuple data : sample){
			String username = data.get(member.username);
			Integer age = data.get(member.age);

			System.out.println("username 	:: " + username);
			System.out.println("age 		:: " + age);
		}
	}
}
