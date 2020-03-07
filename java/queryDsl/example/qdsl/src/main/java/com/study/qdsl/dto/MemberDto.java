package com.study.qdsl.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@Data @RequiredArgsConstructor, @Getter, @Setter가 모두 포함된 롬복 어노테이션
@NoArgsConstructor
@Getter @Setter
public class MemberDto {

	private String username;
	private int age;

	public MemberDto(String username, int age){
		this.username = username;
		this.age = age;
	}
}
