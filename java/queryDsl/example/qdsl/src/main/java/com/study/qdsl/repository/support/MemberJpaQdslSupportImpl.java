package com.study.qdsl.repository.support;

import static com.study.qdsl.entity.QMember.*;
import static com.study.qdsl.entity.QTeam.*;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.QMemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.dto.exression.MemberExpression;
import com.study.qdsl.entity.Member;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;

public class MemberJpaQdslSupportImpl extends QuerydslRepositorySupport implements MemberJpaQdslSupport{

	public MemberJpaQdslSupportImpl() {
		super(Member.class);
	}

	// 별다른 import 작업 없이 부모 클래스인 QuerydslRepositorySupport 내의 from() 을 사용 가능하다.
	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		List<MemberTeamDto> data = from(member)
			.leftJoin(member.team, team)
			.where(
				MemberExpression.userNameEq(condition),
				MemberExpression.teamNameEq(condition),
				MemberExpression.ageBetween(condition)
			)
			.select(
				new QMemberTeamDto(
					member.id.as("memberId"),
					member.username,
					member.age,
					team.id.as("teamId"),
					team.name.as("teamName")
				)
			)
			.fetch();

		return null;
	}

	// Sorting이 불편한 점이 있긴 하지만, 페이징을 간편하게 사용할 수 있도록 지원해주는 점은 장점이다.
	// 쿼리 튜닝 가능 여부는 찾아봐야 알 듯 하다.
	@Override
	public Page<MemberTeamDto> searchPagingSimple(MemberSearchCondition condition, Pageable pageable) {
		JPQLQuery<MemberTeamDto> jpaQuery = from(member)
			.leftJoin(member.team, team)
			.where(
				MemberExpression.userNameEq(condition),
				MemberExpression.teamNameEq(condition),
				MemberExpression.ageBetween(condition)
			)
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			));
//			.offset(pageable.getOffset())
//			.limit(pageable.getPageSize())
//			.fetchResults();

		jpaQuery = getQuerydsl().applyPagination(pageable, jpaQuery);

		QueryResults<MemberTeamDto> fetchResult = jpaQuery.fetchResults();
		List<MemberTeamDto> contents = fetchResult.getResults();
		long count = fetchResult.getTotal();
		return new PageImpl<MemberTeamDto>(contents, pageable, count);
	}
}
