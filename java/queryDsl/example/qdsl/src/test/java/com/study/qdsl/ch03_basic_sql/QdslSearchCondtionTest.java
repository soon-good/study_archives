package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 검색조건 활용
 * 	: and(), or() 연산자의 활용을 알아봤다.
 * 	: and()의 경우 메서드 체이닝 방식을 쓸경우 지저분해 보일 수 있는데,
 * 		and 일 경우에 한해 조건식들을 where 함수의 인자로 넘겨주면 모두 and로 취급된다.
 */
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

	/**
	 * 멤버들 중
	 *  - 이름이 Aladdin 이면서
	 *  - 나이가 30 ~ 40
	 *  인 멤버를 찾아보자.
	 */
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

	/**
	 * and 일 경우에 한해
	 * 	가변인자의 모양처럼 비교 조건을 파라미터로 여러개 보낼수 있다.
	 *
	 * 동적 쿼리 작성시, 이렇게 파라미터로 보내는 방식을 사용할 경우 굉장히 편리해진다.
	 */
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

}
