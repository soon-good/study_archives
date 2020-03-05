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
 * JPQL vs QueryDsl
 * 		JPQL / QueryDsl 코드를 비교해본다.
 *
 * JPQL 의 경우
 *  	런타임에 sql 쿼리 오류를 파악하게 된다.
 *
 *  QueryDsl 의 경우
 *  	컴파일 타임에 미리 sql 쿼리 오류를 파악할 수 있다.
 */
@SpringBootTest
@Transactional
public class JpqlVsQueryDslTest {

	@Autowired
	private EntityManager em;

	private JPAQueryFactory queryFactory;

	/**
	 * 예제 데이터 생성
	 * Member 	: member1, member2, member3, member4
	 * Team 	: teamA, teamB
	 */
	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);		// 인스턴스 em을 바인딩할때 동시성 문제가 되지 않을까? 하고 고민하게 될 수 있다.
													// 클래스의 전역 멤버 필드로 두어도 문제되지 않는다.
													// 여러개의 멀티 쓰레드에서 인스턴스 em에 접근하게 되서 문제가 되지 않을까? 할수도 있다.
													// 여기에 대해서는 동시성 문제 같은 것들을 고민하지 않아도 된다.
													// 스프링 프레임워크가 주입해주는 EntityManager 자체가 멀티쓰레드에 문제가 되지 않도록 설계되어 있다.
													// 여러 멀티 쓰레드에서 들어와도 현재 내 트랜잭이 어디에 걸려있는지에 따라 필요한 곳에 바인딩되도록 분배해준다.

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
	 * username = member1인 데이터를 찾아라.
	 * JPQL 버전)
	 */
	@Test
	public void startJPQL(){

		String sql = "select m from Member m where m.username =: username";

		Member selectedMember1 = em
			.createQuery(sql, Member.class)
			.setParameter("username", "member1")
			.getSingleResult();

		/** sql로 인출해온 데이터의 username 이 "member1"인지 확인 */
		assertThat(selectedMember1.getUsername()).isEqualTo("member1");
	}

	/**
	 * username = member1인 데이터를 찾아라.
	 * QueryDsl 버전)
	 */
	@Test
	public void startQueryDsl(){
//		JPAQueryFactory queryFactory = new JPAQueryFactory(em); // 주석처리하고, queryFactory를 전역필드로 설정
																// before() 구문에서 초기화하도록 변경

		/** QMember("이름");
		 *  - '이름' 은 어떤 QMember인지 구분하는 이름을 주는 것이다. */
		QMember m = new QMember("m");		// QType은 QType("이름") 이렇게 지정하는데,
													// "이름"은 별칭이라고 할 수 있다.

		Member member = queryFactory.select(m)
			.from(m)
			.where(m.username.eq("member1")) // 파라미터 바인딩.
													// 파라미터 바인딩을 하지 않아도
													// JDBC Prepared Statement로 파라미터 바인딩을 자동으로 한다.
			.fetchOne();

		assertThat(member.getUsername()).isEqualTo("member1");
	}
}
