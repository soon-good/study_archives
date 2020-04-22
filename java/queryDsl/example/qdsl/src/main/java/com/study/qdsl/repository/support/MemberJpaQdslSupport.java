package com.study.qdsl.repository.support;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberJpaQdslSupport {
	List<MemberTeamDto> search(MemberSearchCondition condition);
	Page<MemberTeamDto> searchPagingSimple(MemberSearchCondition condition, Pageable pageable);
}
