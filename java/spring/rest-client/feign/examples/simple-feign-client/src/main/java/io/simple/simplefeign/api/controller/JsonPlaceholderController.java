package io.simple.simplefeign.api.controller;

import io.simple.simplefeign.api.dto.Post;
import io.simple.simplefeign.api.external.JsonPlaceholderClient;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonPlaceholderController {

	private final JsonPlaceholderClient jsonPlaceholderClient;

	public JsonPlaceholderController(JsonPlaceholderClient jsonPlaceholderClient){
		this.jsonPlaceholderClient = jsonPlaceholderClient;
	}

	@GetMapping(value = "/json-placeholder/posts")
//	@RequestMapping(value = "/json-placeholder/posts", method = RequestMethod.GET)
	public Object getPosts(){
		List<Post> posts = jsonPlaceholderClient.getPosts();
		return posts;
	}
}
