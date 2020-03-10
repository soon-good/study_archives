package com.study.qdsl.web.member;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.repository.MemberJpaQdslRepository;
import com.study.qdsl.repository.datajpa.MemberDataJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberJpaQdslRepository repository;
	private final MemberDataJpaRepository dataRepository;

	@GetMapping("/v1/members")
	public List<MemberTeamDto> getAllMembers(MemberSearchCondition condition){
		return repository.searchByWhere(condition);
	}

	/** ch06-item5-스프링 데이터 페이징 활용3 - 컨트롤러 개발
	 *  컨트롤러에서 바로 스프링 데이터의 Pageable 을 받을 수 있다.
	 *  포스트맨에서 http://localhost:8080/v2/members?page=0&size=2 조회 고고싱
	 *  참고)
	 *  	orderby 의 경우 스프링 데이터 JPA가 제공하는 OrderSpecifier 를 사용할 수 있다.
	 *  	근데, 특정 경우에 대해서는 잘 동작하지 않는 경우도 있다고 한다.
	 **/
	@GetMapping("/v2/members")
	public Page<MemberTeamDto> getAllMember2(MemberSearchCondition condition, Pageable pageable){
		return dataRepository.searchPageSimple(condition, pageable);
	}

	/** ch06-item5-스프링 데이터 페이징 활용3 - 컨트롤러 개발
	 *  컨트롤러에서 바로 스프링 데이터의 Pageable 을 받을 수 있다.
	 *  포스트맨에서 http://localhost:8080/v3/members?page=0&size=2 조회 고고싱
	 **/
	@GetMapping("/v3/members")
	public Page<MemberTeamDto> getAllMember3(MemberSearchCondition condition, Pageable pageable){
		return dataRepository.searchPageOptimized(condition, pageable);
	}
}
