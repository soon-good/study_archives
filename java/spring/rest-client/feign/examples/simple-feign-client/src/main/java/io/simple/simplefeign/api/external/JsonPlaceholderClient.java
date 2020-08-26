package io.simple.simplefeign.api.external;

import feign.Param;
import feign.RequestLine;
import io.simple.simplefeign.api.dto.Post;
import java.util.List;
import org.springframework.cloud.netflix.feign.FeignClient;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "json-placeholder", url = "${feign.json-placeholder.url}")
public interface JsonPlaceholderClient {

	// 1. @GetMapping 같은 어노테이션은 사용하지 않는다.
//	@GetMapping("/posts")
	@RequestLine("GET /posts")
	List<Post> getPosts();

	@RequestLine("GET /posts/{id}")
	Post get(@Param("id") Long id);

	// 2. PathVariable과 같은 Spring 어노테이션은 netflix 구현체에서 사용불가.
//	Post get(@PathVariable("id") Long id);

}
