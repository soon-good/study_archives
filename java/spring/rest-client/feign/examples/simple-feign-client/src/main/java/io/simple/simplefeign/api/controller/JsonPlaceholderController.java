package io.simple.simplefeign.api.controller;

import io.simple.simplefeign.api.dto.Comment;
import io.simple.simplefeign.api.dto.Post;
import io.simple.simplefeign.api.external.JsonPlaceholderClient;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JsonPlaceholderController {

	private final JsonPlaceholderClient jsonPlaceholderClient;

	public JsonPlaceholderController(JsonPlaceholderClient jsonPlaceholderClient){
		this.jsonPlaceholderClient = jsonPlaceholderClient;
	}

	@GetMapping(value = "/json-placeholder/posts")
	public List<Post> getPosts(){
		List<Post> posts = jsonPlaceholderClient.getPosts();
		return posts;
	}

	@GetMapping(value = "/json-placeholder/posts/{id}")
	public Post getPostsById(@PathVariable(name = "id", required = false) final Long id){
		Post post = jsonPlaceholderClient.get(id);
		return post;
	}

	@GetMapping(value = "/json-placeholder/comments")
	public List<Comment> getPostByParam(@RequestParam(name = "postId") final Long postId){
		List<Comment> comments = jsonPlaceholderClient.getComments(postId);
		return comments;
	}
}
