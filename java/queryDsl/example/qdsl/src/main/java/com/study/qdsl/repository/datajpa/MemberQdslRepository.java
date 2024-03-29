package com.study.qdsl.repository.datajpa;

import static com.study.qdsl.entity.QMember.member;
import static com.study.qdsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.QMemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
@Repository
public class MemberQdslRepository {

	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;

	public MemberQdslRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

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
		return member.age.goe(condition.getAgeGoe());
	}

	private BooleanExpression ageLoe(MemberSearchCondition condition){
		return member.age.loe(condition.getAgeLoe());
	}

	private BooleanExpression ageBetween(MemberSearchCondition condition){
		return member.age.between(condition.getAgeGoe(), condition.getAgeLoe());
	}
}
