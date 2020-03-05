package com.study.qdsl.ch03_basic_sql;

import static com.study.qdsl.entity.QMember.*;
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

@SpringBootTest
@Transactional
public class BasicQTypeExamplesTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
	}

	/**
	 * QType.[엔티티명] 이 static인 것을 이용해 static import로 단순 간결해보이도록 변경
	 */
	@Test
	public void testStaticQType(){
//		QMember member = new QMember("m");  // 이렇게 선언할 수도 있다. 이 코드는 아래와 같이 바꿀수 있다.

//		QMember member = QMember.member;	// QMember.java 내의 코드를 보면 static으로 멤버가 선언되어 있다.
											// 이 코드 역시 바꿀수 있다. QMember 클래스를 static 임포트 하는 것이다.

//		만약 From 절에 필요한 Member 테이블의 Alias가 Member m1과 같이 지정해야 한다면 (셀프 조인 등을 할때 필요하니까)
//		QMember m1 = new QMember("m1");
//		과 같이 지정해주면 된다.

		Member selectedMember = queryFactory.select(member)
			.from(member)
			.where(member.username.eq("member1"))		// SELECT * FROM Member member1 ... 과 같이 테이블에 대한 Alias 지정
			.fetchOne();

		assertThat(selectedMember.getUsername()).isEqualTo("member1");
	}
}
