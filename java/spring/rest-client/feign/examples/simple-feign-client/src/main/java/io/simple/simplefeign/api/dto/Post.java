package io.simple.simplefeign.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Post {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private String userId;

	private Long id;

	private String title;

	private String body;

}
