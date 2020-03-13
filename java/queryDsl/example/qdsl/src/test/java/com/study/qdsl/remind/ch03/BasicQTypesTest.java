package com.study.qdsl.remind.ch03;

import static com.study.qdsl.entity.QMember.*;

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
public class BasicQTypesTest {

	@Autowired
	EntityManager entityManager;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(entityManager);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");

		entityManager.persist(marketingTeam);
		entityManager.persist(analysisTeam);
		entityManager.persist(musicianTeam);

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

		entityManager.persist(john);
		entityManager.persist(susan);
		entityManager.persist(kyle);
		entityManager.persist(stacey);
		entityManager.persist(aladin);
		entityManager.persist(genie);

		entityManager.persist(beethoven);
		entityManager.persist(chopin);
		entityManager.persist(genie2);
		entityManager.persist(nullName);

	}

	@Test
	public void selectAll(){
		List<Member> members = queryFactory.select(member)
			.from(member)
			.fetch();

		for(Member m : members){
			System.out.println( " member :: " + m);
		}
	}
}
