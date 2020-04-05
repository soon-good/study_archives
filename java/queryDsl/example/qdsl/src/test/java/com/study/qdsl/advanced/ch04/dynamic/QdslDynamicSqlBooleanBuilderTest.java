package com.study.qdsl.advanced.ch04.dynamic;

import static com.study.qdsl.entity.QMember.*;
import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
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
 * Ch03 - item4 동적 쿼리 ( 1. BooleanBuilder 를 사용한 방식 )
 *  : BooleanBuilder 에 조건식(BooleanExpression)을 넣어준다.
 *  : 파라미터가 null 이 아닐 경우에 한해 eq(), gt(), goe(), lt(), loe(), ... 의 비교표현식이 동작한다.
 *  : 아직 여기까지는 Mybatis 에서 <where><if test='age != null'> and age=#{age} </if></where> 를 사용하는 것을 java 코드로 옮겨놓은 것에 불과하다.
 *  : 동적쿼리를 조금 더 심화해서 발전해 나가면 비교식을 제품의 특성과 연관지어 하나의 클래스로 묶거나, 객체로 생성할수 있고, 컴포지션을 만들수도 있고 활용은 다양하다.
 *  : 관련 내용은 정리를 계속하면서 차츰 뒤에서 다루게 될 예정
 */
@SpringBootTest
@Transactional
public class QdslDynamicSqlBooleanBuilderTest {

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
	 * 테스트 해보려는 것은 where 조건에 들어가는 파라미터인
	 * usernameParam, ageParam 둘 중 에서 null 인 파라미터는 검색조건에서 제외하여 조건검색하도록 하려 한다.
	 * 즉, 만약 usernameParam, ageParam 둘다 null 일 경우는 where 조건에서 usernameParam, ageParam 을 검색하지 않는다.
	 */
	@Test
	public void dynamicSqlByBooleanBuilder1(){
		String usernameParam = "Genie";
//		Integer ageParam = 210;
		Integer ageParam = null;

		List<Member> result = searchMember1(usernameParam, ageParam);
//		Assertions.assertThat(result.size()).isEqualTo(1);
		assertThat(result.size()).isEqualTo(2);
	}

	private List<Member> searchMember1(String usernameCond, Integer ageCond) {

		BooleanBuilder builder = new BooleanBuilder();

		if(usernameCond != null){
			builder.and(member.username.eq(usernameCond));
		}

		if(ageCond != null){
			builder.and(member.age.eq(ageCond));
		}

		return queryFactory.selectFrom(member)
			.where(builder)
			.fetch();

		/** SQL - username, age 모두 null 이 아닐 경우
			 select
				member0_.member_id as member_i1_1_,
				member0_.age as age2_1_,
				member0_.team_id as team_id4_1_,
				member0_.username as username3_1_
			from
				member member0_
			where
				member0_.username=?
				and member0_.age=?
		 */

		/** SQL - age 가 null 일 경우 (age 조건을 빼고 검색한다.)
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

	/**
	 * 	BooleanBuilder 를 이용한 조합
	 * 		searchMember2(... ) 를 호출
	 **/
	@Test
	public void dynamicSqlByBooleanBuilder2() {
		String usernameParam = "Stacey";
		Integer ageParam = 24;
//		Integer ageParam = null;

		List<Member> result = searchMember2(usernameParam, ageParam);
		assertThat(result.size()).isEqualTo(1);
	}

	/**
	 * BooleanBuilder(BooleanExpression) 을 넣어서 조합하는 예제
	 * 		아직은 Null 처리에 대해 깔끔한 처리를 보장하지 않는 단순예제
	 */
	private List<Member> searchMember2(String usernameCond, Integer ageCond){
		BooleanExpression expr = member.age.eq(ageCond);
		BooleanBuilder builder = new BooleanBuilder(expr);

		if(usernameCond != null){
			builder.and(member.username.eq(usernameCond));
		}

		return queryFactory.selectFrom(member)
			.where(builder)
			.fetch();
	}
}
