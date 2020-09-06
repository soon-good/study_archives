package io.study.resilience4jfeign1.api.external.jsonplaceholder;

import feign.RequestLine;
import io.study.resilience4jfeign1.api.external.jsonplaceholder.dto.Post;
import java.util.List;

public interface JsonPlaceholderClient {
	@RequestLine("GET /posts")
	List<Post> getPosts();
}
