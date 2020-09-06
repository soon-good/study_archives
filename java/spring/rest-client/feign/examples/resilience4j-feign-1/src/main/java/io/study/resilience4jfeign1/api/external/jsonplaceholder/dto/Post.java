package io.study.resilience4jfeign1.api.external.jsonplaceholder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Post {

	private Long id;

	@JsonProperty(access = Access.WRITE_ONLY)
	private Long userId;

	private String title;

	private String body;
}
