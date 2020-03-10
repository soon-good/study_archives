package com.study.qdsl.repository.custom;

import static com.study.qdsl.entity.QMember.*;
import static com.study.qdsl.entity.QTeam.*;
import static org.springframework.util.StringUtils.*;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.QMemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return
			queryFactory.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				member.team.id.as("teamId"),
				member.team.name.as("teamName")
			))
			.from(member)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.leftJoin(member.team, team)
			.fetch();
	}

	private BooleanExpression userNameEq(MemberSearchCondition condition){
		return hasText(condition.getUsername()) ? member.username.eq(condition.getUsername()) : null;
	}

	private BooleanExpression teamNameEq(MemberSearchCondition condition){
		return hasText(condition.getTeamName()) ? team.name.eq(condition.getTeamName()) : null;
	}

	private BooleanExpression ageGoe(MemberSearchCondition condition){
		return condition.getAgeGoe() == null ? null : member.age.goe(condition.getAgeGoe());
	}

	private BooleanExpression ageLoe(MemberSearchCondition condition){
		return condition.getAgeLoe() == null ? null : member.age.loe(condition.getAgeLoe());
	}

	private BooleanExpression ageBetween(MemberSearchCondition condition){
		return member.age.between(condition.getAgeGoe(), condition.getAgeLoe());
	}

	/** ch06-item3-스프링 데이터 페이징 활용1 - QueryDsl 페이징 연동
	 * limit(), offset() 을 통해 페이지네이션을 적용한다.
	 * fetchResults() 를 통해 결과를 얻어낸다.
	 *
	 * fetchResults() 가 호출 될때
	 *  - 실제 쿼리는 두번 호출된다. (컨텐츠 쿼리 + 카운트 쿼리)
	 *  - 카운트 쿼리 실행시 필요없는 order by는 제거된다.
	 * */
	@Override
	public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
		QueryResults<MemberTeamDto> fetchResults = queryFactory
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				member.team.id.as("teamId"),
				member.team.name.as("teamName")
			))
			.from(member)
			.leftJoin(member.team, team)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchResults(); // fetchResults() 를 사용하면 content 쿼리와 count 쿼리 두번을 호출한다.

		List<MemberTeamDto> results = fetchResults.getResults();
		long total = fetchResults.getTotal();
		return new PageImpl<MemberTeamDto>(results, pageable, total);
	}

	/**
	 * ch06-item3-스프링 데이터 페이징 활용1 - QueryDsl 페이징 연동
	 *
	 * 조회성능 최적화를 위해
	 * 	- fetch(), fetchCount() 를 분리한다.
	 * 	- fetchResults() = fetch() + fetchCount() 인데 여기서 둘을 분리해내서 따로 부른다.
	 * */
	@Override
	public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {

		List<MemberTeamDto> results = queryFactory.select(
			new QMemberTeamDto(
				member.id.as("memberId"),
				member.username.as("username"),
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			)
		)
		.from(member)
		.leftJoin(member.team, team)
		.where(
			userNameEq(condition),
			teamNameEq(condition),
			ageGoe(condition),
			ageLoe(condition)
		)
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();

		/**
		 * ch06-item3-스프링 데이터 페이징 활용1 - QueryDsl 페이징 연동
		 *
		 * 가끔 count 쿼리 작성시 leftJoin 이 필요가 없을 경우가 있다.
		 * 위의 예를 들면, 모든 멤버들의 카운트를 구하는 경우이다.
		 * 따라서 카운트를 구할때는 팀의 이름이 필요가 없다.
		 *
		 * fetchResults()를 사용하면 count() 쿼리, fetch() 가 묶여있어서 분리하기 힘들다.
		 * 이것을 최적화 하기 위해서는 count()를 먼저하고 데이터가 없으면 fetch()를 안하거나 하는 것도 좋은 방법 중의 하나이다.
		 *
		 *
		 * 다음 장에서 이것을 더 최적회하는 방법들을 정리한다.
		 */
		long count = queryFactory.select(member)
			.from(member)
//			.leftJoin(member.team, team) // 필요 없을 때도 있다.
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetchCount();

		return new PageImpl<MemberTeamDto>(results, pageable, count);
	}

	/** ch06-item4-스프링 데이터 페이징 활용2 - countQuery 최적화
	 *   : 불필요하게 count 쿼리를 날리지 않도록 불필요하게 count 쿼리를 날리지 않도록, 필요한 경우에만 조건적으로 쿼리를 실행하도록
	 *   	Spring Data 에서 제공하는 PageableExecutionUtils 를 사용한다.
	 *
	 * 	 : 1) 페이지의 시작이면서 컨텐츠 사이즈가 페이지의 사이즈보다 작을 때
	 * 	 	- ex) 페이지의 사이즈는 30개로 정했는데 전체 DB에서는 20개의 글밖에 존재하지 않을 때
	 *
	 * 	 : 2) 마지막 페이지
	 * 	 	- offset + 컨텐츠의 사이즈 를 통해 전체 사이즈를 구한다.
	 * 	  	- 카운트 쿼리를 할 필요가 없다.
	 *
	 * 	 : PageableExecutionUtils.getPage(content, pageable, LongSupplier)
	 * 	 	- LongSupplier 에 람다식을 전달한다.
	 * 	    - 람다식 내부에서 fetchCount()를 한다.
	 * 	    - 이 람다식은 위의 1), 2) 의 경우에는 실행되지 않고
	 * 	    	: 이미 얻어온 contents의 사이즈를 구하거나
	 * 	    	: offset + 컨텐츠 사이즈 를 통해 전체 사이즈를 구한다.
	 **/
	@Override
	public Page<MemberTeamDto> searchPageOptimized(MemberSearchCondition condition, Pageable pageable) {
		List<MemberTeamDto> results = queryFactory.select(new QMemberTeamDto(
			member.id.as("memberId"),
			member.username.as("username"),
			member.age,
			member.team.id.as("teamId"),
			member.team.name.as("teamName")
		))
		.from(member)
		.leftJoin(member.team, team)
		.where(
			userNameEq(condition),
			teamNameEq(condition),
			ageGoe(condition),
			ageLoe(condition)
		)
		.offset(pageable.getOffset())
		.limit(pageable.getPageSize())
		.fetch();

		/** Query 를 람다 표현식에 저장 */
		JPAQuery<Member> countSql = queryFactory
			.select(member)
			.from(member)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			);

		// SQL을 람다 표현식으로 감싸서 람다 또는 메서드 레퍼린스를 인자로 전달해준다.
		// PageableExecutionUtils 에서 위의 1),2) 에 해당하면 SQL 호출을 따로 하지 않는다.
//		return PageableExecutionUtils.getPage(results, pageable, ()->countSql.fetchCount());
		// or
		return PageableExecutionUtils.getPage(results, pageable, countSql::fetchCount);
	}
}
