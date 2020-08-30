package io.simple.simplefeign.api.external;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.simple.simplefeign.api.dto.Comment;
import io.simple.simplefeign.api.dto.Post;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class JsonPlaceholderClientTest {

	private JsonPlaceholderClient jsonPlaceholderClient;

	@BeforeEach
	public void setup() throws Exception{
		jsonPlaceholderClient = Feign.builder()
			.client(new OkHttpClient())
			.encoder(new JacksonEncoder())
			.decoder(new JacksonDecoder())
			.logger(new Slf4jLogger(JsonPlaceholderClient.class))
			.logLevel(Logger.Level.BASIC)
			.target(JsonPlaceholderClient.class, "https://jsonplaceholder.typicode.com");
	}


	@Test
	@DisplayName("json-placeholder > posts")
	void testJsonPlaceholder(){
		List<Post> posts = jsonPlaceholderClient.getPosts();
		System.out.println(posts);
	}

	@Test
	@DisplayName("json-placeholder > posts/{id}")
	void testJsonPlaceholderById(){
		Post post = jsonPlaceholderClient.get(1L);
		System.out.println(post);
	}

	@Test
	@DisplayName("json-placeholder > comments")
	void testCommentsById(){
		List<Comment> comments = jsonPlaceholderClient.getComments(1L);
		System.out.println(comments);
	}

//	@Disabled
//	@Test
//	@DisplayName("json-placeholder > posts/{id} > requestBody")
//	void testJsonPlaceholderByRequestBody(){
//		Post post = new Post();
//		post.setId(1L);
//		Post byBody = jsonPlaceholderClient.getByBody(post);
//		System.out.println(byBody);
//	}
}
