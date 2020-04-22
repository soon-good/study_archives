package com.study.qdsl.dto.exression;

import static com.study.qdsl.entity.QMember.member;
import static com.study.qdsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.study.qdsl.dto.condition.MemberSearchCondition;

public class MemberExpression {

	public static BooleanExpression userNameEq(MemberSearchCondition condition){
		return hasText(condition.getUsername()) ? member.username.eq(condition.getUsername()) : null;
	}

	public static BooleanExpression teamNameEq(MemberSearchCondition condition){
		return hasText(condition.getTeamName()) ? team.name.eq(condition.getTeamName()) : null;
	}

	public static BooleanExpression ageGoe(MemberSearchCondition condition){
		return member.age.goe(condition.getAgeGoe());
	}

	public static BooleanExpression ageLoe(MemberSearchCondition condition){
		return member.age.loe(condition.getAgeLoe());
	}

	public static BooleanExpression ageBetween(MemberSearchCondition condition){
		return member.age.between(condition.getAgeGoe(), condition.getAgeLoe());
	}
}
