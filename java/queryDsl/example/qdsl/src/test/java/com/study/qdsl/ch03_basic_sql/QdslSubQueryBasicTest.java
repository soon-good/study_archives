package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
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
 * QueryDsl - Ch03-12. 서브쿼리
 *  : 서브쿼리는 com.querydsl.jpa.JPAExpressions 를 사용한다.
 *  : eq(), goe(), in()
 *  : JPA 서브쿼리의 한계점은 from절에 서브쿼리를 쓰는 것 (인라인 뷰)을 지원하지 않는다.
 *
 *  참고) 인라인 뷰 미지원
 *  : 당연히 queryDsl도 인라인 뷰는 지원하지 못한다.
 *  : queryDsl은 단순히 JPQL의 빌더 역할을 하는 것이기 때문이다.
 *
 *  참고) select 절 서브쿼리
 *  : QueryDSL 사용시 하이버네이트의 구현체를 사용시 select절의 서브쿼리를 지원한다.
 *  : 과거 에는 안되던 스펙이었다.
 *
 *  from 절 서브쿼리(인라인뷰) 미지원에 대한 해결방안
 *  : 서브쿼리를 JOIN 으로 변경한다.(가능한 상황도 있고, 불가능한 상황도 있다. 서브쿼리는 굉장히 높은 확율로 join으로 변경 가능하다.)
 *  : 애플리케이션에서 쿼리를 2번 분리해서 실행한다. (성능이 아주 중요한 시스템이 아닌 이상, 2번 이상 하는게 나을 수 있다. 물론 문제가 될때도 있다.)
 *  : nativeSQL을 사용한다.
 *
 *  : 인라인뷰를 사용할 때 보통 악용되는 경우가 매우 많다고 지적하고 있다.
 *  : DB에 있는 로우 데이터(raw data)만 가져와야 하는데 화면에 필요한 각종 정보들도 같이 SQL에 섞어서 쓰는 경우도 종종 있기 때문이다.
 *  : SQL은 데이터를 가져오는 것에만 집중하고, Date 포맷 맞추거나, 애플리케이션에서의 필요한 작업들은 애플리케이션 및 화면에서 하도록 해주는 것이 좋다.
 *  : 어떻게든 화면에 맞출려다 보니깐 From 절안에 From 절, From 절안에 From .... 이런 경우를 너무나도 많이 봐왔다고 이야기하는군.
 *  : 현대적인 애플리케이션들은 애플리케이션로직에서 많이 풀고 뷰도 프리젠테이션 로직에서 풀고 이런 경우가 많다.
 *  : SQL에서 다 풀어내려고 하다보니까 SQL이 복잡해진다는 이야기를 함.
 *
 *  : DB는 데이터를 퍼 오는 용도로만 쓰자
 *  : filter에서 잘짤라내고 Group By 중요하다는 이야기를 함.
 *  : 한방 쿼리에 대해 미신이 있는데 이게 좋지 않다고 한다.
 *  : 실시간 트래픽이 많은 서비스이면 쿼리 한방 한방이 아깝다(ㅋㅋ)고 한다. 이런 이유로 캐시나 이런거 엄청 쓰고 있다고 이야기함
 *  : 어드민이나 이런 성능이 중요하지 않은 경우는 쿼리를 여러번 보낸다.
 *  : 그러면서 SQL AntiPatterns 책을 추천
 **/
@SpringBootTest
@Transactional
public class QdslSubQueryBasicTest {

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
	 * 나이가 가장 많은 회원 조회
	 */
	@Test
	public void subQueryEq(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		QMember subMember = new QMember("subMember");

		List<Member> data = queryFactory.selectFrom(member)
			.where(
				member.age.eq(
					JPAExpressions
						.select(subMember.age.max())
						.from(subMember)
				)
			)
			.fetch();

		assertThat(data).extracting("age").containsExactly(251);

		/**
			select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_
			where
				member0_.age=(
					select
						max(member1_.age)
					from
						member member1_
				)
		*/
	}

	/**
	 * 나이가 평균 이상인 회원 찾기
	 */
	@Test
	public void subQueryGoe(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		QMember subMember = new QMember("subMember");

		List<Member> goeData = queryFactory.selectFrom(member)
			.where(
				member.age.goe(
					JPAExpressions
						.select(subMember.age.avg())
						.from(subMember)
				)
			)
			.fetch();

		System.out.println("goeData: " + goeData);
		assertThat(goeData).extracting("age").contains(251,210,100);
	}

	/**
	 * In
	 */
	@Test
	public void subQueryIn(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		QMember subMember = new QMember("subMember");

		List<Member> inData = queryFactory.selectFrom(member)
			.where(
				member.age.in(
					JPAExpressions
						.select(subMember.age)
						.from(subMember)
						.where(subMember.age.gt(99))
				)
			)
			.fetch();

		System.out.println("inData :: " + inData);
	}

	/**
	 * select 절에 subquery
	 * 각 사용자의 이름 옆에 평균나이도 함께 보여주기
	 */
	@Test
	public void selectSubQuery(){
		QMember member = QMember.member;
		QTeam team = QTeam.team;
		QMember subMember = new QMember("subMember");

		List<Tuple> data = queryFactory
			.select(
				member.username,
				JPAExpressions
					.select(subMember.age.avg())
					.from(subMember)
			)
			.from(member)
			.fetch();

		for(Tuple t : data){
			System.out.println("data >>> " + t);
		}
	}
}
