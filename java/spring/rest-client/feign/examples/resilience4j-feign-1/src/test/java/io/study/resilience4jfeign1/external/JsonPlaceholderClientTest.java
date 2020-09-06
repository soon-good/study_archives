package io.study.resilience4jfeign1.external;

import io.study.resilience4jfeign1.api.external.jsonplaceholder.JsonPlaceholderClient;
import io.study.resilience4jfeign1.api.external.jsonplaceholder.dto.Post;
import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class JsonPlaceholderClientTest {

	@Autowired
	JsonPlaceholderClient jsonPlaceholderClient;

	public void testGetPosts(){
		List<Post> posts = jsonPlaceholderClient.getPosts();
	}
	
}
