package io.simple.simplefeign.api.external;

import feign.Param;
import feign.RequestLine;
import io.simple.simplefeign.api.dto.Comment;
import io.simple.simplefeign.api.dto.Post;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "json-placeholder", url = "${feign.json-placeholder.url}")
public interface JsonPlaceholderClient {

	// 1. 참고)
	// netflix 구현체에서는 @GetMapping 같은 어노테이션은 사용하지 않는다.
	// (netflix 구현체는 지원을 중단했다. openfeign 을 사용해야 한다.)
	@GetMapping("/posts")
	@RequestLine("GET /posts")
	List<Post> getPosts();

	@GetMapping(value = "/posts/{id}")
	@RequestLine("GET /posts/{id}")
	Post get(@PathVariable("id") Long id);

//	@RequestLine("GET /posts")
//	Post getByBody(@RequestBody Post post);

	// 2. 참고)
	// PathVariable과 같은 Spring 어노테이션은 netflix 구현체에서 사용불가.
	// (netflix 구현체는 지원을 중단했다. openfeign 을 사용해야 한다.)
//	Post get(@PathVariable("id") Long id);

	@GetMapping(value = "/comments")
	@RequestLine("GET /comments?postId={postId}")
	List<Comment> getComments(@Param("postId") Long postId);

}
