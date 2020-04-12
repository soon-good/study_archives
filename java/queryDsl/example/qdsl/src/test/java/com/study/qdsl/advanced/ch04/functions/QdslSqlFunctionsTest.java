package com.study.qdsl.advanced.ch04.functions;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class QdslSqlFunctionsTest {

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
	public void testSqlFunction1(){
		QMember member = QMember.member;
		List<String> data = queryFactory
			.select(
				Expressions.stringTemplate(
					"function('replace', {0}, {1}, {2})",
					member.username, "Genie", "지니"
				)
			)
			.from(member)
			.where(member.username.eq("Genie"))
			.fetch();
	}

	@Test
	public void testSqlFunction2(){
		QMember member = QMember.member;
		List<String> data = queryFactory
			.select(
				Expressions.stringTemplate(
					"function('lower',{0})",
					member.username
				)
			)
			.from(member)
			.where(member.username.eq("Genie"))
			.fetch();
	}
}
