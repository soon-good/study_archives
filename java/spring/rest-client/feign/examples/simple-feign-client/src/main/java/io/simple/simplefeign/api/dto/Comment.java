package io.simple.simplefeign.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Comment {
	@JsonProperty(access = Access.READ_ONLY)
	private Long postId;

	private Long id;

	private String name;

	private String email;

	private String body;
}
