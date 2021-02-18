# CircuitBreaker 설정 및 기본개념

참고할만한 자료들

- 공식문서
  - [resilience4j documentation](https://resilience4j.readme.io/docs/getting-started)

- 국내 블로그



# 1. 의존성 추가

## spring-cloud

openfeign

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-openfeign -->
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
  <version>2.2.4.RELEASE</version>
</dependency>
```



## resilience4j

> resilience4j 는 netflix hystrix의 오픈소스 버전이고, hystrix는 더 이상 개발없이 유지보수만 진행되고있다. netflix에서는 resilience4j를 사용하도록 권고하는 중.  

circuit breaker 라이브러리로는 hystrix가 유명하다. hystrix는 netflix에서 제작한 라이브러리로, Spring 기본 라이브러리로 사용되었다. 현재는 netflix에서 더이상 추가 개발 없이 유지보수만 하기로 결정, resilience4j를 사용하는 것을 권고했다.  

```xml
<!-- https://mvnrepository.com/artifact/io.github.resilience4j/resilience4j-feign -->
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-feign</artifactId>
  <version>1.5.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.github.resilience4j/resilience4j-spring-boot2 -->
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot2</artifactId>
  <version>1.5.0</version>
</dependency>
```



## micrometer-registry-prometheus

참고 : [Prometheus 란?](https://micrometer.io/docs/registry/prometheus)

```xml
<!-- https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus -->
<dependency>
  <groupId>io.micrometer</groupId>
  <artifactId>micrometer-registry-prometheus</artifactId>
  <version>1.5.4</version>
</dependency>
```



prometheus를 추가하고나면 아래와 같이 spring-boot-starter-actuator 를 추가해주어야 한다. ([참고](https://github.com/prometheus/client_java/issues/452)) 

```xml
<!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
  <version>2.2.4.RELEASE</version>
</dependency>
```



또는 resilience4j 의 resilience4j-micrometer를 추가해주자.

```xml
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-micrometer</artifactId>
  <version>1.2.0</version>
</dependency>
```



## feign-annotation-error-decoder

```xml
<!-- https://mvnrepository.com/artifact/io.github.openfeign/feign-annotation-error-decoder -->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-annotation-error-decoder</artifactId>
    <version>1.3.0</version>
</dependency>

```



# 2. Java config

CircuitBreaker를 설정하는 CircuitConfig의 전체 소스는 제일 아래에 정리해두었다.

## 1) CircuitConfig

Resilience4j를 활용해 CircuitBreaker를 적용하는 것은 단순히 Feign Client만을 CircuitBreaker 처리할 수 있는 것은 아니다. 이 문서에서는 일단 FeignClient를 CircuitBreaker처리하는 부분을 다뤄보고자 한다.   

  

Resilience4j에서 제공하는 Resilience4jFeign을 이용해 CircuitBreaker 처리를 할 때 보통 두가지의 방식중의 하나를 이용해 FeignClient 인스턴스를 생성해낸다. 두 가지의 방식은 아래와 같다.  

- Resilience4jFeign.Builder 를 사용하는 방식
- FeignDecorators.Builder 를 사용하는 방식

이 두 가지의 방식의 핵심은 Builder 패턴의 클래스를 inner class로 두어 필요한 정보를 build()하는 것이라는 점이다. 아직 현재로서는 Resilience4jFeign.Builder를 이용해 FeignClient 인스턴스를 생성하는 것까지만 해보았다. 나머지에 대해서는 추후에 공부해보면 좋을 것 같다. (FeignDecorators.Builder 를 사용하는 방식은 공식 document에서 자주 예제로 다루고 있기는 하다.)  

  

CircuitConfig에 작성한 설정 파일의 원리를 요약해보면 다음과 같다.

- FeignClient를 Resilience4J의 FeignDecorators 타입으로 감싼다. 
- Resilience4jFeign의 builder() 를 통해 FeignClient 객체로 만들어낸다.
  - Resilience4jFeign 클래스의 builder() 메서드
    - Resilience4jFeign 클래스 내의 inner class인 Resilience4jFeign.Builder 타입의 인스턴스를 생성한다.
    - 이 Resilience4jFeign.Builder 클래스는 feign.Feign.Builder 클래스를 상속받는다.
    - feign.Feign.Builder 클래스는 eoncoder, decoder, retryer, options 등등 Feign에 필요한 필수적인 필드들을 가지고 있으며, setter 메서드를 대체할 수 있는 Builder 패턴형식의 메서드들을 제공해준다.
  - 주의할 점
    - **FeignDecorators.Builder**와 **Resilience4jFeign.Builder** 를 **혼동해서 사용하지 말아야 한다는 점**이다.
  - 만약 이렇게 하면 어떤 부분에서는 FeignDecorators를 사용하지만, 어떤 부분은 Resilience4jFeign을 사용하는 코드가 되어 디버깅이 어려워진다.
  - 내부 소스의 구조를 30분만 차분히 보면 이해도가 훨씬 높아진다는 것을 참고하자.



### FeignClient 를 Resilience4J CircuitBreaker에 감싸서 생성하기

> Resilience4jFeign 클래스의 동작원리는 소스코드를 잠깐 봤는데, 정리해보면 추후에 굉장히 유용한 자료가 될 것 같다. 이에 대해서는 추후에 정리해봐야 할 듯하다.  

  

- Resilience4jFeign 이 제공하는 Feign Client 기능을 하는 클래스가 있다.
- Resilience4jFeign내에 등록할 CircuitBreaker 인스턴스를 FeignDecorators 타입의 인스턴스로 생성해 등록한다.
  - **FeignDecorators 인스턴스 생성)**  
    FeignDecorators 인스턴스 내에 CircuitBreaker 인스턴스를 감싸서 build() 한 결과이다. 
  - **Resilience4jFeign.Builder 내에 decorator 등록)**  
    - Resilience4jFeign.builder() 메서드는 Resilience4jFeign.Builder 타입의 인스턴스를 생성해주는 역할을 한다.
    - Resilience4jFeign.builder() 메서드 호출시 특정 decorator를 지정해서 이미 만들어서 인자값으로 전달한 특정 CircuitBreaker에 대한 설정을 기반으로 feign.Feign.Builder 내에서 연결해 사용할 수 있도록 해준다.
- feign.Feign.Builder를 기반으로 해서 decorator, JacksonEncoder, JacksonDecoder, Retryer, LogLevel, Logger 등의 필요한 인스턴스들을 등록한다.
- feign.Feign.Builder의  target() 메서드를 통해 FeignClient 타입으로의 변환을 수행한다.
- 이 target() 메서드는  newInstance() 메서드를 통해 새로운 인스턴스를 생성하는데, 구체적인 동작은 더 살펴봐야 할 듯하다.(추후 정리 예정 !TODO)
- Resilience4jFeign.Builder는 feign.Feign.Builder의 확장클래스(자식클래스)이며 아래의 역할을 수행한다.
  - feign.Feign.Builder 클래스의 대부분의 기능을 Wrapping 하는 역할을 한다.
  - build() 메서드를 제공해준다. (이 예제에서는 사용하지 않는다. 다른 예제문서에서 한번 다뤄볼 예정이다.)



```java
@Configuration 
public class JsonPlaceholderCircuitConfig {
  private long connectTimeout = 300;
	private long readTimeout = 300;
  
  @Bean
	JsonPlaceholderClient getJsonPlaceholderClient(CircuitBreakerRegistry registry){
		CircuitBreaker circuitBreaker = registry.circuitBreaker("FEIGN_API");

		FeignDecorators decorators = FeignDecorators.builder()
			.withCircuitBreaker(circuitBreaker)
			.build();

		return Resilience4jFeign.builder(decorators)
			.encoder(new JacksonEncoder())
			.decoder(new JacksonDecoder())
			.retryer(new CircuitNonRetryer())
			.logLevel(Level.FULL)
//			.errorDecoder(new ErrorDecoder.Default())
			.logger(new Slf4jLogger(JsonPlaceholderClient.class))
			.options(new Request.Options(connectTimeout, TimeUnit.MILLISECONDS, readTimeout, TimeUnit.MILLISECONDS, false))
			.target(JsonPlaceholderClient.class, "http://jsonplaceholder.typicode.com");
	}
}
```



