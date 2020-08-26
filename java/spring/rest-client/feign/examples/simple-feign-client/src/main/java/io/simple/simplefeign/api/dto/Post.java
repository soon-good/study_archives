package io.simple.simplefeign.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Post {

	private String userId;

	private Long id;

	private String title;

	private String body;

}
