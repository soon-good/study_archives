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



# 예제 - UPDATE/DELETE



# 테스트 코드