package com.study.qdsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

/**
 * @Data 는 Getter, Setter, NoArgsConstructor, toString, equals 등의 다양한 메서드를 생성해주는 역할
 */
@Data
public class MemberTeamDto {

	private Long memberId;
	private String username;
	private int age;
	private Long teamId;
	private String teamName;

	@QueryProjection
	public MemberTeamDto(Long memberId, String username, int age, Long teamId, String teamName){
		this.memberId = memberId;
		this.username = username;
		this.age = age;
		this.teamId = teamId;
		this.teamName = teamName;
	}
}
