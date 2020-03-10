package com.study.qdsl.repository.custom;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
public interface MemberJpaCustom {
	public List<MemberTeamDto> search(MemberSearchCondition condition);
	/** ch06-item3-스프링 데이터 페이징 활용1 - QueryDsl 페이징 연동 */
	public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable);

	/** ch06-item3-스프링 데이터 페이징 활용1 - QueryDsl 페이징 연동 */
	public Page<MemberTeamDto> searchPageComplex (MemberSearchCondition condition, Pageable pageable);

	/** ch06-item4-스프링 데이터 페이징 활용2 - countQuery 최적화 */
	public Page<MemberTeamDto> searchPageOptimized(MemberSearchCondition condition, Pageable pageable);
}
