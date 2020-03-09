package com.study.qdsl.repository;

import static com.study.qdsl.entity.QMember.*;
import static com.study.qdsl.entity.QTeam.*;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.QMemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

/**
 * 참고)
 * 방법 2) 에 비해서 방법 1)이 QueryDsl 테스트 코드 작성시 조금 더 편리하다.
 * 방법 2) 의 경우는 테스트코드 작성시 외부에서 의존성 라이브러리를 주입해야 하므로 조금 불편
 *
 * 참고) EntityManager 는 스프링 컨테이너에서 오직 하나인 인스턴스 객체인데, 동시성 문제가 있지 않을까요??
 * 		JPAQueryFactory 또한 EntityManager 인스턴스를 주입받아 사용하고 있습니다. 동시성 문제가 없나요??
 *
 * 		EntityManager 는 싱글턴으로 스프링 컨테이너에서 주입받은 객체이다.
 * 		JPAQueryFactory 의 동작 역시 EntityManager 인스턴스에 의존하고 있다.
 * 		EntityManager 를 스프링에서 사용시 동시성 문제가 없이 트랜잭션 단위로 모두 분리되도록 처리해주도록 설계되어 있다.
 * 		EntityManager 에는 실제 동작을 책임지는 역할을 하는 객체가 아닌 Proxy 하는 객체, 즉, 대리로 해주는 객체(가짜객체)를 주입해준다.
 * 		주입받은 프록시 역할을 하는 객체가 각각 다른 곳에 바인딩 되도록 라우팅을 해준다.
 *
 * 		더 자세한 내용은 김영한 님 JPA 책 13.1 트랜잭션 범위의 영속성 컨텍스트를 참고하면 된다.
 */
@Repository
//@RequiredArgsConstructor
public class MemberJpaQdslRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	/** 방법 1)
	 * JPAQueryFactory 의 인스턴스를 내부에서 생성한다.
	 **/
	public MemberJpaQdslRepository(EntityManager em) {
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
	}

	/** 방법 2)
	 * QueryDslConfig.java 에 선언했듯이 @Bean 으로 JPAQueryFactory 인스턴스를 생성하는 빈을 등록한다.
	 * EntityManager, JPAQueryFactory를 인자로 받아 의존성 주입하는 생성자를 작성한다.
	 * 		또는
	 * @RequiredArgsConstructor 를 선언하고 의존성 주입하는 생성자를 따로 두지 않는다.
	 * final 선언된 멤버 필드들을 모두 생성자에 의존성 주입해준다.
	 */
//	public MemberJpaQdslRepository(EntityManager em, JPAQueryFactory queryFactory) {
//		this.em = em;
//		this.queryFactory = queryFactory;
//	}

	public void save(Member member){
		em.persist(member);
	}

	public Optional<Member> findById(Long id){
		Member foundMember = em.find(Member.class, id);
		return Optional.ofNullable(foundMember);
	}

	public List<Member> findAll() {
		return queryFactory.selectFrom(member)
			.fetch();
	}

	public List<Member> findByUsername(String username){
		return queryFactory.selectFrom(member)
			.where(member.username.eq(username))
			.fetch();
	}

	public List<MemberTeamDto> searchByBuilder(MemberSearchCondition condition){

		BooleanBuilder builder = new BooleanBuilder();

		/**
		 * 웹에서 넘어오는 파라미터 들은 null 로 넘어올 때도 있고 ""으로 넘어올 때도 있다.
		 * 이런 경우에 대해 편리함을 제공해주는 라이브러리가 SpringFramework 에서 제공해주는 StringUtils::hasText() 이다.
		 **/
//		if(null, "")
		if (StringUtils.hasText(condition.getUsername())) {
			builder.and(member.username.eq(condition.getUsername()));
		}
		if (StringUtils.hasText(condition.getTeamName())) {
			builder.and(member.team.name.eq(condition.getTeamName()));
		}
		if(condition.getAgeGoe() != null){
			builder.and(member.age.goe(condition.getAgeGoe()));
		}
		if(condition.getAgeLoe() != null){
			builder.and(member.age.loe(condition.getAgeLoe()));
		}

		return queryFactory
			.select(new QMemberTeamDto(
						member.id.as("memberId"),
						member.username,
						member.age,
						member.team.id.as("teamId"),
						member.team.name.as("teamName")
					))
					.from(member)
					.where(builder)
					.leftJoin(member.team, team)
					.fetch();
	}

	/**
		Ch05 - item3 :: 동적 쿼리와 성능 최적화 조회 (where 절과 BooleanExpression 활용)
		 : QueryDsl 의 Where절에 BooleanExpression 반환 메서드를 통한 동적 쿼리 예제
		 : 특정 조건 (ConditionDto) Dto를 생성해 조건 값들을 가지는 객체로 동적생성 (좋다!!)
				: 검색 조건 처리 함수, null 처리 공통화 (좋다!!)

		 : 테스트 코드 작성
		 : 항상 경계해야 하는 것이지만, 동적 쿼리는 항항 null 을 주의해야 한다.
	 */
	public List<MemberTeamDto> searchByWhere(MemberSearchCondition condition){
		return queryFactory
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				member.team.id.as("teamId"),
				member.team.name.as("teamName")
			))
			.from(member)
			.where(
				userNameEq(condition.getUsername()),
				teamNameEq(condition.getTeamName()),
				ageGoe(condition.getAgeGoe()),
				ageLoe(condition.getAgeLoe())
			)
			.leftJoin(member.team, team)
			.fetch();
	}

	/**
	 * where 동적 파라미터의 장점
	 * 	: 조건 검사 함수를 재사용할 수 있다.
	 * 	: 이런 조건들은 공통화하여 공통 사용 클래스를 두어 static 함수로 제공해도 되고
	 * 	: Enum 으로 비교 루틴들을 정형화 하여 제공할 수도 있고, 활용범위가 다양해진다.
	 *
	 * 	: 이런 공통 함수들을 또 Enum 으로 한번 더 묶어서 isValid()함수 등을 제공할 수도 있다.
	 */
	private BooleanExpression userNameEq(String username) {
		return StringUtils.isEmpty(username) ? null : member.username.eq(username);
	}

	private BooleanExpression teamNameEq(String teamName) {
		return StringUtils.isEmpty(teamName) ? null : team.name.eq(teamName);
	}

	private BooleanExpression ageGoe(Integer ageGoe) {
		return ageGoe == null ? null : member.age.goe(ageGoe);
	}

	private BooleanExpression ageLoe(Integer ageLoe) {
		return ageLoe == null ? null : member.age.loe(ageLoe);
	}

	private BooleanExpression ageBetween(int ageLoe, int ageGoe){
		return ageGoe(ageGoe).and(ageLoe(ageLoe));
	}
}
