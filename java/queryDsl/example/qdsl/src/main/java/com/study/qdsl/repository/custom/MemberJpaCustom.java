package com.study.qdsl.repository.custom;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import java.util.List;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
public interface MemberJpaCustom {
	public List<MemberTeamDto> search(MemberSearchCondition condition);
}
