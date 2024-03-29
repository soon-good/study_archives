# Spring Boot 에서 Feign 사용하기

프로젝트를 진행하다보면 외부 API에 데이터를 가져오거나, 외부 API 서버에 요청을 보내 특정 동작을 수행하도록 요청하는 경우(결제 요청 등등)가 자주 있다. 이런 경우 javascript 에서 ajax 통신을 통해 REST API에 요청을 보내는 것도 하나의 방법이다. 하지만 요청을 보낼 REST API서버의 위치가 

- 사내 망에서만 접근이 가능하거나
- 서버단에서 요청을 보내는 것이 더 맞는 경우
- Java로 크롤링을 하는 경우 

등등의 경우도 생각해봐야 한다. 이런 경우는 서버단에서 외부 API를 호출해야 한다. Spring/Maven 에서는 다른 서버의 REST API와 통신하기 위한 기본적인 기능들을 제공하는 라이브러리를 제공해주고 있다. 

- [springframework.http.client](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/client/package-summary.html)
- [jersey-client](https://mvnrepository.com/artifact/org.glassfish.jersey.core/jersey-client)



그런데 위의 라이브러리는 REST API 통신을 위한 정말 기초적인 기능들만을 제공해준다. REST API 의 상태를 체크해서 예외상황이라 판단하여 해당 API를 끝내거나 하는 기능들을 부가적으로 제공하지는 않는다. 즉, 사용자가 직접 구현해야 한다. 상용서비스들은 비지니스 로직이 굉장히 길고 호출들도 다양하다. 그런데 만약 외부 REST API 호출을 할때 네트워크 장애 등의 예외상황에 대한 처리를 직접 구현하지 않을 경우, 그 중간의 흐름에서 특정 외부 API로 호출을 했으나 응답이 없을경우 CONNECTION ERROR 등의 RESPONSE가 오기 전까지는 기다려야 하게 되고, 이 구간이 병목구간이 되어 운영상의 성능을 저하시키게 될 가능성 또한 있다.  

  

이런 이유로 circuitbreaker 라는 개념을 다뤄볼 예정인데... circuitBreaker에 대해서는 다음에 정리할 예정이고 오늘은 Feign 라이브러리를 정리해보려 한다. Circuit Breaker 는 Feign을 사용할 경우에 대한 설정 역시 제공해주고 있다.  

  

오늘 사용해볼 Feign 라이브러리는 REST API Client 를 구현할 때 사용하는 로직들을 공통화하고, 추상화하여 라이브러리화 한 오픈소스이다. 처음 사용해봤지만, 정말 편리하다. 가져오려는 외부 REST API에 대한 스프링 설정을 통해

- Http Client 의 종류
- Json Encoder/Json Decoder 의 종류
- Logger
- 외부 API에 따른 별도의 Log Level, Loger의 종류

를 조립식으로 설정하고 입맛대로 커스터마이징해서 코드를 공통화 할 수 있다는 장점이 있다.  



서론이 길었다. Feign 을 사용하는 장점을 짧고 굵게 요약해보면, "굉장히 편리하고 변경이 용이하다. 개발 속도가 빠르다. 모듈화가 굉장히 잘 되어있다"는 점이 장점이다.



# 1. 예제로 사용할 외부 API

샘플 REST API 를 라이브로 제공해주는 온라인 웹 서비스들이 있다. 이곳에서 제공하는 API를 외부 API로 해서 Feign 클라이언트를 작성해보자.

- [https://jsonplaceholder.typicode.com/](https://jsonplaceholder.typicode.com/)
- [https://reqres.in/](https://reqres.in/)
- [http://dummy.restapiexample.com/](http://dummy.restapiexample.com/)
- [https://fakerestapi.azurewebsites.net/](https://fakerestapi.azurewebsites.net/)



# 2. 의존성 (build.gradle)

## dependencyManagement

gradle을 사용할 때 spring cloud의 dependencyManagement 를 추가해주어야 하는데, feign 버전이 올라갈 수록 라이브러리가 깨지는 곳이 많았다. stackoverflow를 전전해본 결과 가장 많이 쓰이는 dependencyManagement는 Greenwich 였다.

```groovy
dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:Greenwich.RELEASE"
    }
}
```

  

## openfeign

build.gradle의 전체 코드는 아래에 정리해 두었다. spring-cloud 에서 제공하는 feign 의존성의 종류는 두가지 이다.

- netflx에서 제공하는 feign
  - 라이브러리들의 목록은 아래와 같다.
  - [com.netflix.feign - mvnrepository](https://mvnrepository.com/artifact/com.netflix.feign)
- spring-cloud 에서 제공하는 openfeign  
  - 라이브러리들의 목록은 아래와 같다.
  - [io.github.openfeign - mvnrepository](https://mvnrepository.com/artifact/io.github.openfeign)

이 중에서 openfeign을 사용해 예제를 정리할 예정이다.  

  

netflix 는 유지보수를 하는 단계이고, 더 이상 개발을 하지는 않는다고 한다. 직접 설정해본 결과 netflix로 의존성 설정을 하는 것은 어려웠다. 의존성이 깨지는 부분도 많았다. 그리고 가장 큰 이유로 특정 회사에 종속적인 라이브러리를 사용하게 되면 호환성이 떨어질 것 같다는 생각이 커서 openfeign을 선택하게 되었다.  

  

### openfeign 기반 설정시 사용한 라이브러리들

#### spring-starter 계열 라이브러리들

- spring-cloud-starter-config
- spring-cloud-starter-openfeign

```groovy
//-- spring-cloud-starter-config
// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-config
compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-config', version: '2.2.4.RELEASE'

//-- spring-cloud-starter-openfeign
// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-openfeign', version: '2.2.4.RELEASE'
```

  

#### feign-core

- feign-core

```groovy
//-- feign-core
// https://mvnrepository.com/artifact/io.github.openfeign/feign-core
compile group: 'io.github.openfeign', name: 'feign-core', version: '11.0'
```



#### openfeign utility 성 라이브러리들

- feign-okhttp
  - http client 엔진을 선택가능하다.
- feign-jackson
  - openfeign이 사용할 json encoding, json decoding 을 수동으로 지정할 수 있다.
  - jackson, gson 등의 잘 알려진 json encoding 라이브러리를 선택가능하다.
- feign-annotation-error-decoder
  - 에러 로그를 조금 더 자세히 보고자 할 때 사용

```groovy
//-- openfeign utility (okhttp, jackson, etc)
// https://mvnrepository.com/artifact/io.github.openfeign/feign-okhttp
compile group: 'io.github.openfeign', name: 'feign-okhttp', version: '11.0'
// https://mvnrepository.com/artifact/io.github.openfeign/feign-jackson
compile group: 'io.github.openfeign', name: 'feign-jackson', version: '11.0'
// https://mvnrepository.com/artifact/io.github.openfeign/feign-annotation-error-decoder
compile group: 'io.github.openfeign', name: 'feign-annotation-error-decoder', version: '1.2.0'
```



## 전체 코드

```groovy
plugins {
    id 'org.springframework.boot' version '2.3.3.RELEASE'
    id 'io.spring.dependency-management' version '1.0.10.RELEASE'
    id 'java'
}

group = 'io.simple'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '1.8'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
//        mavenBom "org.springframework.cloud:spring-cloud-dependencies:Finchley.SR2"
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:Greenwich.RELEASE"
    }
}


dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'mysql:mysql-connector-java'
    compile group: 'org.mariadb.jdbc', name: 'mariadb-java-client', version: '2.6.2'
    testCompile group: 'com.h2database', name: 'h2', version: '1.4.200'
    annotationProcessor 'org.projectlombok:lombok'

    //-- spring-cloud-starter-config
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-config
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-config', version: '2.2.4.RELEASE'

    //-- spring-cloud-starter-openfeign
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign
    compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-openfeign', version: '2.2.4.RELEASE'

    //-- feign-core
    // https://mvnrepository.com/artifact/io.github.openfeign/feign-core
    compile group: 'io.github.openfeign', name: 'feign-core', version: '11.0'

    // netflix
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-feign
    // compile group: 'org.springframework.cloud', name: 'spring-cloud-starter-feign', version: '1.4.7.RELEASE'

    //-- openfeign utility (okhttp, jackson, etc)
    // https://mvnrepository.com/artifact/io.github.openfeign/feign-okhttp
    compile group: 'io.github.openfeign', name: 'feign-okhttp', version: '11.0'
  
    // https://mvnrepository.com/artifact/io.github.openfeign/feign-jackson
		compile group: 'io.github.openfeign', name: 'feign-jackson', version: '11.0'

    // https://mvnrepository.com/artifact/io.github.openfeign/feign-annotation-error-decoder
    compile group: 'io.github.openfeign', name: 'feign-annotation-error-decoder', version: '1.2.0'


    //-- test
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}

test {
    useJUnitPlatform()
}

```



# 3. Java Config

외부 API 하나에 대한 Feign Client는 @Configuration 이나 @Component 로 제공되어야 한다. 이 점은 조금 불편하기는 하다. 애플리케이션의 설정이 외부 API에 대한 변경사항에 종속적으로 변한다는 사실은 조금 약점이긴 하다. 하지만 이런 불편함을 뛰어넘어서 편리함과 유연함을 제공해준다.  

```java
package io.simple.simplefeign.config;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import io.simple.simplefeign.api.external.JsonPlaceholderClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

	@Bean
	public JsonPlaceholderClient jsonPlaceholderClient(){
		return Feign.builder()
			.client(new OkHttpClient())
			.encoder(new JacksonEncoder())
			.decoder(new JacksonDecoder())
			.logger(new Slf4jLogger(JsonPlaceholderClient.class))
			.logLevel(Logger.Level.FULL)
			.target(JsonPlaceholderClient.class, "https://jsonplaceholder.typicode.com");
	}
}
```



# 4. application.yml

```yaml
# 이 부분은 FeignClientConfig 내에서 설정해줄 수도 있다. 
# 가끔가다 있는 spring profile로 phase를 분리할 때 phase별로 외부 API의 URL이 달라질수도 있는 상황을 가정해
# yml 파일내에 json-placeholder FeignClient의 url 을 명시해주었다.
feign:
  json-placeholder:
    url: https://jsonplaceholder.typicode.com

spring:
  datasource:
    url: jdbc:mariadb://localhost:23306/ec2_web_stockdata
    username: testuser
    password: 1111
    hikari:
      auto-commit: true
    driver-class-name: org.mariadb.jdbc.Driver

```



# 5. FeignClient 작성하기

## Dto

Post.java

```java
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
```



Comment.java

```java
package io.simple.simplefeign.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class Comment {

	@JsonProperty(value = "postId", access = Access.AUTO)
	private Long postId;

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("email")
	private String email;

	@JsonProperty("body")
	private String body;
}
```



## FeignClient

### GET 메서드 예제

FeignClient 는 interface 내에 @GetMapping, @RequestLine 등의 어노테이션을 사용하여 통신 가능하다. 이러한 interface를 구현해서 사용하는 경우는 아직까지는 못봤다. 굳이 그럴 이유가 없어서 실제로 그렇게 해본 사람들은 없는 듯 하다.

```java
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
```



- @FeignClient
  - name 에는 FeignClient의 이름을 지정해준다.
  - url 에는 요정하려는 외부 REST API의 base url을 지정해준다. 
    - application.yml의 feign.json-placeholder.url 에는 https://jsonplaceholder.typicode.com 을 입력해두었다.
- @RequestLine("GET /posts/{id}")
  - https://jsonplaceholder/typicode.com/posts/{id} 에 GET 요청을 한다.
- @GetMapping
  - openfeign 을 사용할 때 @RequestMapping, @GetMapping을 Feign Client 내의 외부요청 메서드에 명시해주어야 한다.
- @Param
  - @RequestParam 과 같은 역할을 한다.
  - 외부 API에 요청할 때에 @Param 어노테이션을 사용한다.



### POST 메서드 예제





# 6. Test Code

Web 을 구동시켜서 확인해보기 전에 Test Code로 동작을 확인해보자. Test 의존성은 Junit5를 사용했다. junit4를 사용할 경웅에도 몇개의 코드만 수정해서 적용하면 된다.  

!TODO setup() 에서 FeignClient 객체인 JsonPlaceholderClient 타입의 인스턴스를 생성하고 있다. 이 부분에 대해서는 추후에 정리할 예정 (목요일~!!!! ㅋㅋㅋ)

```java
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

}
```



# 7. Application.java

Application위에 @EnableFeignClients 를 추가해준다. 또는 따로 설정 파일로 분리해서 설정 파일내에 @EnableFeignClients를 해주는 것도 좋은 방식이다.

```java
package io.simple.simplefeign;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeignApplication.class, args);
	}

}
```



# 8. Controller

JsonPlaceholderController.java

```java
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
```



# 9. 테스트용 서버 구동하기

외부 API서버를 사용하지 않을 경우 테스트용 REST API 서버 구동을 해서 결과를 확인할 수 있다.

