# spring data jpa CRUD



- Request 데이터 Dto
- API 요청에 대한 Controller
- 트랜잭션, 도메인 기능간의 순서를 보장하는 Service



> 보통 Service에서 비지니스 로직을 처리한다고 오해를 할 수 있다. 하지만 그렇지 않다.  
>
> Service는 
>
> - 트랜잭션
> - 도메인 간의 순서 보장
>
> 의 역할만을 수행한다.  



![Spring Web Layer](./img/Spring_Web_Layer.png)



- Web Layer
  - 컨트롤러(@Controller), JSP/Freemarker 등 뷰 템플릿 영역
  - @Filter, 인터셉터, 컨트롤러 어드바이스(@ControllerAdvice) 등 외부 요청과 응답에 대한 전반적인 영역
- Service Layer
  - @Service에 사용되는 서비스 영역
  - Controller, Dao의 중간영역에서 사용되는 영역
  - @Transactional 이 사용되어야 하는 영역
- Repository Layer
  - Database와 같은 데이터 저장소에 접근하는 영역 (DAO 영역)
- Dtos
  - Dto (Data Transfer Object)
  - Dto : 계층 간에 데이터 교환을 위한 객체
  - Dtos는 Dto들의 영역을 이야기한다.
  - ex)
    - 뷰 템플릿 엔진에서 사용될 객체나 Repository Layer에서 결과로 넘겨준 객체 등이 이들을 이야기한다.  
- Domain Model
  - 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화 시킨 것
  - 택시 앱을 예로 들면 배차, 탑승, 요금 등을 도메인이라고 할 수 있다.
  - @Entity가 사용된 영역 역시 도메인 모델이라고 이해할 수 있다.
  - 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니다.



JPA에서는 Web(Controller), Service, Repository, Dto, Domain 이 5가지의 레이어에서  

- Domain

이 비지니스 처리를 담당하도록 작성하게 할 수 있다.  

기존에(Mybatis, iBatis 등) 서비스로 처리하던. 방식은. **트랜잭션 스크립트**라고 한다. 주문 취소로직을 작성한다면 아래와 같다.  



### 서비스로 처리할 때(트랜잭션 스크립트)의 코드

### psuedo code

```java
@Transactional
public Order cancelOrder(int orderId){
  1) 데이터베이스로부터 주문정보(Orders), 결제정보(Billing), 배송정보(Delivery) 조회
  2) 배송 취소를 해야 하는지 확인
  3) if(배송 중이라면){
    배송 취소로 변경
  }
  4) 각 테이블에 취소상태 update
}
```



### 실제 코드

```java
@Transactional
public Order cancelOrder(int orderId){
  //1)
  OrdersDto order = ordersDao.selectOrders(orderId);
  BillingDto billing = billingDao.selectBilling(orderId);
  DeliveryDto delivery = deliveryDao.selectDelivery(orderId);
  
  //2)
  String deliveryStatus = delivery.getStatus();
  
  //3)
  if("IN_PROGRESS".equals(deliveryStatus)){
    delivery.setStatus("CANCEL");
    deliveryDao.update(delivery);
  }
  
  //4)
  order.setStatus("CANCEL");
  ordersDao.update(order);
  
  billing.setStatus("CANCEL");
  deliveryDao.update(billing);
  
  return order;
}
```



### 도메인 모델에서 처리할 경우

- order, billing, delivery가 각자 본인의 취소 이벤트 처리를 한다.
- 서비스 메서드는 트랜잭션과 도메인 간의 순서만 보장해준다.

```java
@Transactional
public Order cancelOrder(int orderId){
  //1)
  Orders order = ordersRepository.findById(orderId);
  Billing billing = billingRepository.findByOrderId(orderId);
  Delivery delivery = deliveryRepository.findByOrderId(orderId);
  
  //2,3)
  delivery.cancel();
  
  //4) 
  order.cancel();
  billing.cancel();
  
  return order;
}
```



# 예제 - INSERT

- PostsApiController
- PostsSaveRequestDto
- PostsService



## Controller, Service



스프링에서 Bean을 주입받는 방식은 아래의 세가지 방식이 있다.

- @Autowired
- setter
- 생성자

이 세가지 중 가장 권장하는 방식이 생성자로 주입받는 방식이다. @Autowired는 이제 권장하지 않는 방식이라고 한다.  

아래 코드에는 Autowired가 없다. Autowired 없이도 Bean을 생성해 주입할 수 있다.  

  

> 생성자로 Bean객체를 받도록 하면 @Autowired와 같은 효과를 볼 수 있다.

  

아래의 코드(PostsApiController, PostsService) 에서는 생성자를 선언하지 않았지만 롬복의 @RequiredArgsConstructor가 생성자를 생성하도록 한다. @RequiredArgsConstructor는 클래스 내의 final이 선언된.  모든 필드를 인자값으로 하는 생성자를 생성해준다.  

  

생성자를 직접 안 쓰고 롬복 어노테이션을 사용한 이유는 

- 해당 클래스의 의존성 관계가 변경될 때마다 생성자 코드를 계속해서 수정하는 번거로움을 해결하기 위해서 이다.

  

> 롬복 어노테이션을 사용하게 되면서 
>
> - 해당 컨트롤러에 새로운 서비스를 추가하거나
> - 기존 컨트롤러를 제거하는 등의 상황이 발생해도 
>
> 생성자 코드는 전혀 손대지 않으므로 간편하다.



### PostsApiController

```java
package com.stock.data.web.posts;

import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

	private final PostsService postsService;

	@PutMapping("/api/v1/posts")
	public Long save(@RequestBody PostsSaveRequestDto requestDto){
		return postsService.save(requestDto);
	}
}
```



### PostsService

```java
package com.stock.data.web.posts;

import com.stock.data.domain.posts.PostsRepository;
import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostsService {
	private final PostsRepository postsRepository;

	@Transactional
	public Long save(PostsSaveRequestDto requestDto) {
		return postsRepository
					.save(requestDto.toEntity())
					.getId();
	}
}
```



## Dto

Entity 클래스를 절대, Request/Response 클래스로 사용하면 안된다.  

Entity 클래스는 데이터베이스와 맞닿는 핵심 클래스이다. 

Entity 클래스를 기준으로 테이블이 생성되고, 스키마가 변경된다. 화면 변경은 아주 사소한 기능변경이고, 이를 위해 테이블과 관련된 Entity 클래스를 변경하는 것은 큰 변경이기 때문이다.  

수많은 서비스 클래스, 비즈니스 로직들이 Entity 클래스를 기준으로 동작한다. Entity 클래스가 변경되면 여러 클래스에 영향을 끼치지만, Request, Response 용 Dto는 View를 위한 클래스 이므로 자주 변경이 되어도 된다. View는 정말 자주 변경되는 영역이므로 Dto를 따로 만들어 쓰는것을 권장하고 있다.  

> DB Layer와 View Layer의 역할 분리를 철저히 하는 것이 좋습니다. 실제로 Controller 에서 결과값으로 여러 테이블을 조인해서 주어야 하는 경우가 빈번하므로 Entity 클래스만으로 표현하기 어려운 경우가 많다.  
>
> 꼭 Entity 클래스와 Controller 에서 쓸 Dto는 분리해서 사용해야 한다.  



PostsSaveRequestDto

```java
package com.stock.data.web.posts.dto;

import com.stock.data.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {

	private String title;
	private String content;
	private String author;

	@Builder
	public PostsSaveRequestDto(String title, String content, String author){
		this.title = title;
		this.content = content;
		this.author = author;
	}

	public Posts toEntity() {
		return Posts.builder()
				.title(title)
				.content(content)
				.author(author)
				.build();
	}
}
```



# 테스트 코드 - INSERT

JPA 코드를 테스트 할때에는 @WebMvcTest를 사용하지 않는다. @WebMvcTest에서는 JPA기능이 동작하지 않기 때문이다. @WebMvcTest 사용시에는 Controller, ControllerAdvice 등 외부 연동과 관련된 부분만 활성화된다. (HelloController 를 테스트 할 때에는 @WebMvcTest를 사용했었다.)  

  

이런 이유로 JPA 테스트 시에는 

- @SpringBootTest
- TestRestTemplate

만을 활용해 테스트한다.



```java
package com.stock.data.web.posts;

import static org.assertj.core.api.Assertions.assertThat;

import com.stock.data.domain.posts.PostsRepository;
import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PostsRepository postsRepository;

	@After
	public void tearDown() throws Exception{
		postsRepository.deleteAll();
	}

	@Test
	public void Posts_등록된다() throws Exception{
		String title = "타이틀 ... ";
		String content = "content";

		PostsSaveRequestDto requestDto =
			PostsSaveRequestDto.builder()
				.title(title)
				.content(content)
				.author("어떤 작가님")
				.build();

		String url = "http://localhost:"
						+ port
						+ "/api/v1/posts";

		ResponseEntity<Long> responseEntity =
			restTemplate.postForEntity(url, requestDto, Long.class);

		assertThat(responseEntity.getStatusCode())
			.isEqualTo(HttpStatus.OK);

		assertThat(responseEntity.getBody())
			.isGreaterThan(0L);
	}
}
```



# 예제 - UPDATE



## Domain

- Posts

Posts 도메인 내에서는 update(content, title) 함수를 추가했다.

별도의 JPA함수 호출 없이 @Transactional 이 붙어 있는 곳에서 

post.update("asdfa", "asdfasdf") 와 같이 수행하면 이 곳에서 Transaction의 순서를 보장하여 저장해준다.  

이것이 가능한 이유는 Posts 클래스에 @Entity 어노테이션이 붙었고 이 의미는 JPA의 Repository 영역에서 이 Entity 를 관리한다는 의미인 것으로 보인다. 자세한 것은 더 공부하자.

```java
package com.stock.data.domain.posts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Posts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 500, nullable = false)
	private String title;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	private String author;

	@Builder
	public Posts(String title, String content, String author){
		this.title = title;
		this.content = content;
		this.author = author;
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}
}

```



## Dto

- PostsResponseDto
- PostsUpdateRequestDto

단순 데이터 조회결과 return, 단순 데이터 수정의 경우 모두 Posts에 대한 Dto를 만들어준다. (조회와 수정 모두 같은 내용을 보여주지는 않으므로 각각 다른 Dto를 가지고 있어도 된다.)



### PostsResponseDto

findById를 Get요청으로 처리할 때   

Post   라는 이름의 Entity에서 받아온 결과중 일부분의 필드만을 Dto로 보여주는 것이기 때문에 Entity 객체를 그대로 받아서 처리하는 단순 로직을 가지고 있다.

```java
package com.stock.data.web.posts.dto;

import com.stock.data.domain.posts.Posts;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsResponseDto {

	private Long id;

	private String title;
	private String content;
	private String author;

	public PostsResponseDto (Posts entity){
		this.id = entity.getId();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.author = entity.getAuthor();
	}
}
```



### PostsUpdateRequestDto

단순히 화면에서 title, content를 PostsUpdateRequestDto 객체로 묶어서 전달 받는다. 

```java
package com.stock.data.web.posts.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsUpdateRequestDto {
	private String title;
	private String content;

	@Builder
	public PostsUpdateRequestDto(String title, String content){
		this.title = title;
		this.content = content;
	}
}
```



참고를 위해 PostUpdateRequestDto가 RequestBody로 사용되는 Controller에서의 메서드를 남겨본다.

```java
@RequiredArgsConstructor
@RestController
public class PostsApiController{
  ...
  @PutMapping("/api/v1/posts/{id}")
  public Long update(@PathVariable Long id,
                    @RequestBody PostUpdateRequestDto requestDto){
    return postsService.update(id, requestDto);
  }   
}
```





## Controller, Service

- PostsApiController
- PostsService



### PostsApiController

```java
package com.stock.data.web.posts;

import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import com.stock.data.web.posts.dto.PostsResponseDto;
import com.stock.data.web.posts.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

	private final PostsService postsService;

  // ...
  
	@PutMapping("/api/v1/posts/{id}")
	public Long update( @PathVariable Long id,
						@RequestBody PostsUpdateRequestDto requestDto){
		return postsService.update(id, requestDto);
	}

	@GetMapping("/api/v1/posts/{id}")
	public PostsResponseDto findById(@PathVariable Long id){
		return postsService.findById(id);
	}
}
```



### PostsService

```java
package com.stock.data.web.posts;

import com.stock.data.domain.posts.Posts;
import com.stock.data.domain.posts.PostsRepository;
import com.stock.data.web.posts.dto.PostsResponseDto;
import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import com.stock.data.web.posts.dto.PostsUpdateRequestDto;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostsService {
	private final PostsRepository postsRepository;

	@Transactional
	public Long update(Long id, PostsUpdateRequestDto requestDto) {

		Posts posts = postsRepository.findById(id)
			.orElseThrow(()->new IllegalArgumentException("사용자가 없습니다. ID = " + id));

		posts.update(requestDto.getTitle(), requestDto.getContent());
		return id;
	}

	public PostsResponseDto findById(Long id) {
		Posts post = postsRepository.findById(id)
			.orElseThrow(()->new IllegalArgumentException("사용자가 없습니다. ID = "+ id));
		return new PostsResponseDto(post);
	}
}

```



위에서 언급했듯이 Posts 클래스내에 update라는 함수가 존재한다. 그리고 Posts클래스에는 @Entity 애노테이션이 달려있다. 그리고 @Transactional이 붙어있는 update 메서드 내에서 postsRepository 를 이용해 직접 update기능을 호출하는 쿼리를 수행하는 부분이 없다.  

이것은 

-  JPA의 영속성 컨텍스트

대분에 가능한 것이다. 영속성 컨텍스트는 엔티티를 영구 저장하는 환경이다. 일종의 논리적 개념이라고 볼수 있으며, JPA의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있는지 아닌지로 갈린다.  

JPA의 엔티티 매니저(Entity Manager)가 활성화 된 상태로(Spring Data Jpa를 쓴다면 기본 옵션) 트랜잭션 안에서 데이터베이스에서 데이터를 가져오면 이 데이터는 영속성 컨텍스트가 유지된 상태이다.  

이 상태에서 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에 해당 테이블에 변경분을 반영한다. 즉, **Entity 객체의 값만 변경하면 별도로 Update쿼리를 날릴 필요가 없다**는 의미이다. 이 개념을 더티체킹(dirty checking이라 한다.)

좀더 자세한 설명은 [블로그 - 기억보다는 기록을](https://jojoldu.tistory.com/415) 을 참고하면 된다.



# 테스트 코드

- PostsApiControllerTest

```java
package com.stock.data.web.posts;

import static org.assertj.core.api.Assertions.assertThat;

import com.stock.data.domain.posts.Posts;
import com.stock.data.domain.posts.PostsRepository;
import com.stock.data.web.posts.dto.PostsSaveRequestDto;
import com.stock.data.web.posts.dto.PostsUpdateRequestDto;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private PostsRepository postsRepository;

	@After
	public void tearDown() throws Exception{
		postsRepository.deleteAll();
	}

	@Test
	public void Posts_등록된다() throws Exception{
		String title = "타이틀 ... ";
		String content = "content";

		PostsSaveRequestDto requestDto =
			PostsSaveRequestDto.builder()
				.title(title)
				.content(content)
				.author("어떤 작가님")
				.build();

		String url = "http://localhost:"
						+ port
						+ "/api/v1/posts";

		ResponseEntity<Long> responseEntity =
			restTemplate.postForEntity(url, requestDto, Long.class);

		assertThat(responseEntity.getStatusCode())
			.isEqualTo(HttpStatus.OK);

		assertThat(responseEntity.getBody())
			.isGreaterThan(0L);
	}

	@Test
	public void Posts_수정된다() throws Exception{
		Posts savedPosts = postsRepository.save(
			Posts.builder()
				.title("제목 1 ")
				.content("내용 1")
				.author("author")
				.build()
		);

		Long updateId = savedPosts.getId();
		String expectedTitle = "title2";
		String expectedContent = "content2";

		PostsUpdateRequestDto requestDto =
			PostsUpdateRequestDto.builder()
				.title(expectedTitle)
				.content(expectedContent)
				.build();

		String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

		HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

		ResponseEntity<Long> responseEntity =
			restTemplate.exchange(
				url, HttpMethod.PUT, requestEntity, Long.class
			);

		assertThat(responseEntity.getStatusCode())
				.isEqualTo(HttpStatus.OK);

		assertThat(responseEntity.getBody())
				.isGreaterThan(0L);

		List<Posts> all = postsRepository.findAll();

		assertThat(all.get(0).getTitle())
			.isEqualTo(expectedTitle);
		assertThat(all.get(0).getContent())
			.isEqualTo(expectedContent);
	}
}
```



# JPA Auditing

JPA Auditing으로 생성시간/수정시간을 자동화해보자

보통 Entity(엔티티)는 해당 데이터의 생성시간, 수정시간을 포함하고 있다. 언제 만들어졌는지, 언제 수정되었는지 등은 추후 유지보수에 있어서 굉장히 요긴하게 쓰이기 때문이다.  

  

이런 이유로 매번 DB insert, update 하기 전에 날짜 데이터를 등록/수정하는 코드가 여기저기 들어가게 된다.  

```java
public void savePosts(){
  //...
  posts.setCreateDate(new LocalDate());
  postsRepository.save(posts);
}
```

이렇게 날짜에 관련된 정보를 create/update하는 코드는 굉장히 중복되는 면도 많고, 코드가 지저분해지기도 한다. 이런 문제를 해결하기 위해 JPA Auditing을 사용한다.



## LocalDate 사용

Java8 에서부터 LocalDate, LocalDateTime이 등장했는데, 예전의 Java 기본 날짜 타입인 Date의 문제점을 고친 타입이므로 Java8을 사용하거 있을 경우 LocalDate, LocalDateTime은 안 쓸 이유가 없다.  

hibernate에서 LocalDate, LocalDateTime이 데이터베이스에 매핑되는 것이 제대로 되지 않는 현상이 Hibernate 5.2.10 버전 이후로 해결되었다고 한다. (스프링 부트 1.x의 경우 별도로 Hibernate 5.2.10을 사용하도록 설정이 필요하다. 스프링 부트 2.x버전을 사용하면 기본적으로 5.2.10을 사용중이므로 별다른 설정없이 바로 사용하면 된다)



>기존 Date, Calendar 클래스의 문제점
>
>1. 불변(변경이 불가능한) 객체가 아니다.  
>
>   멀티 스레드 환경에서 언제든 문제가 발생한다.
>
>2. Calendar 는 월(Month) 값 설계가 잘못됬다.  
>   2월은 1, 3월은 2,... 등으로 값을 표기하고 있다. 
>
>이런 문제들을 JodaTime이라는 오픈소스로 해결해왔었다. Java8에서는 LocalDate를 통해 해결한다. (자세한 내용은 - [Java 날짜와 시간 API](https://d2.naver.com/helloworld/645609) 를 참고. )



## domain - BaseTimeEntity

domain 패키지 바로 아래에 BaseTimeEntity 클래스를 생성한다. BaseTimeEntity 클래스는 모든 Entity의 상위 클래스가 되어 Entity들의  createdDate, modifiedDate를 자동으로 관리하는 역할을 수행하게 된다.

```java
package com.stock.data.domain;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime modifiedDate;
}
```

- @MappedSuperClass  
  - 하위클래스에게서 매핑되는 수퍼클래스임을 명시하는 역할
  - 멤버필드가 DB 컬럼으로 인식되도록 하는 역할
    - JPA Entity 클래스들이 BaseTimeEntity를 상속할 경우 @CreatedDate, @LastModifiedDate로 선언한 멤버필드들도 컬럼으로 인식하도록 해주는 역할을 수행한다.  
- @EntityListeners(AuditingEntityListener.class)  
  - BaseTimeEntity 클래스에 Auditing 기능을 포함시킨다.
- @CratedDate
  - Entity가 생성되어 저장될 때의 시간이 자동 저장된다.
- @LastModifiedDate
  - 조회한 Entity의 값을 변경할 때의 시간이 자동 저장된다.

## domain - Posts

Auditing 기능이 적용되길 원하는  Entity클래스가 방금 작성한  BaseTimeEntity클래스르 상속받도록 해준다.

```java
public class Posts extends BaseTimeEntity{
  // ...
}
```



## Main - @EnableJpaAuditing

Application 클래스에 @EnableJpaAuditing 을 추가해 Spring Boot 애플리케이션이 JpaAuditing기능이  On 되어 있는 상태로 동작하도록 해준다.

```java
package com.stock.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Application {
	public static void main(String [] args){
		SpringApplication.run(Application.class, args);
	}
}
```



## 테스트 코드

```java
  @Test
	public void BaseTimeEntity_등록(){
		LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0,0, 0);
		postsRepository.save(
			Posts.builder()
				.title("title")
				.content("content")
				.author("author")
				.build()
		);

		List<Posts> postsList = postsRepository.findAll();

		Posts post = postsList.get(0);

		System.out.println("====== createdDate :: "
			+ post.getCreatedDate()
			+ ", modifiedDate :: "
			+ post.getModifiedDate()
		);

		assertThat(post.getCreatedDate()).isAfter(now);
		assertThat(post.getModifiedDate()).isAfter(now);
	}
```

