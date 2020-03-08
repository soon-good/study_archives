package com.study.qdsl.advanced.ch04.dynamic;

import static com.study.qdsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Ch03 - item5 :: 동적 쿼리 - Where 다중 파라미터 사용
 *  : 여전히 null 체크는 주의해서 해야 한다. (하지만, 이것 역시 Mybatis 에서도 null 체크를 주의해서 했었다.)
 *  : 동적쿼리를 조금 더 심화해서 발전해 나가면 비교식을 제품의 특성과 연관지어 하나의 클래스로 묶거나, 객체로 생성할수 있고, 컴포지션을 만들수도 있고 활용은 다양하다.
 *  : 관련 내용은 정리를 계속하면서 차츰 뒤에서 다루게 될 예정이다.
 *
 *  : 실제 개발시 제품에 대한 비교문의 특성을 메서드로 만들어 명세화/정형화가 가능하고, 재사용 역시 가능하다는 점에서 장점이 있다.
 *  : 예) isServiceable(...), isCouponAvailavle(...), isDeviceAvailable(...) 등등
 */
@SpringBootTest
@Transactional
public class QdslMultiWhereParamTest {

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
	public void dynamicSqlByMultiWhereParamTest1(){
		String pUserName = "Stacey";
//		Integer pAge = 24;
		Integer pAge = null;

		List<Member> data = searchData1(pUserName, pAge);
		assertThat(data.size()).isEqualTo(1);
	}

	/**
	 * userNameEq(), ageEq() 와 같은 메서드가 많아져서 알아보기 힘들다고 생각할 수도 있다.
	 * 하지만, 실제 개발시에 보는 소스는
	 *
	 * queryFactory.selectFrom(member)
	 * 	.where(...)
	 * 	.fetch();
	 *
	 * 이고, where 절 내부가 복잡해질 수록 파악이 어렵다. (서브쿼리를 예로 들면 그러하다.)
	 */
	private List<Member> searchData1(String pUserName, Integer pAge) {
		return queryFactory.selectFrom(member)
			.where(userNameEq(pUserName), ageEq(pAge))
			.fetch();

		/**
			파라미터 바인딩) 만약 pAge가 null 이면 위의 query에서의 파라미터 바인딩은 아래와 같이 된다.
			queryFactory.selectFrom(member)
				.where("Stacey", null)
				.fetch();

			SQL) SQL 은 아래와 같이 생성된다. null 인 파라미터는 제외하고 조회하고 있다.
			select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_
			where
				member0_.username=?
		*/
	}

	private BooleanExpression ageEq(Integer pAge) {
		return pAge == null ? null : member.age.eq(pAge);
	}

	private BooleanExpression userNameEq(String pUserName) {
		return pUserName == null ? null : member.username.eq(pUserName);
	}

	/**
	 * 위에서 만들었던 userNameEq(), ageEq() 함수를 한번 더 조합해서
	 * allEq()를 만들어 where 절에 넘길 수도 있다.
	 * where 절 다중 파라미터는 조립이 가능하다는 것이 장점이다.
	 */
	@Test
	public void dynamicSqlByMultiWhereParamTest2(){
		String pUserName = "Stacey";
		Integer pAge = 24;

		List<Member> data = searchData2(pUserName, pAge);
		assertThat(data.size()).isEqualTo(1);
	}

	private List<Member> searchData2(String pUserName, Integer pAge) {
		return queryFactory.selectFrom(member)
			.from(member)
			.where(eqAll(pUserName, pAge))
			.fetch();
	}

	private BooleanExpression eqAll(String pUserName, Integer pAge){
		return userNameEq(pUserName).and(ageEq(pAge));
	}
}
