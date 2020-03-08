package com.study.qdsl.web.member;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.repository.MemberJpaQdslRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberJpaQdslRepository repository;

	@GetMapping("/v1/members")
	public List<MemberTeamDto> getAllMembers(MemberSearchCondition condition){
		return repository.searchByWhere(condition);
	}
}
