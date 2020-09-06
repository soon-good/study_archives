package io.study.resilience4jfeign1.config;

import feign.Logger.Level;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.feign.FeignDecorators;
import io.github.resilience4j.feign.Resilience4jFeign;
import io.study.resilience4jfeign1.api.external.jsonplaceholder.JsonPlaceholderClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonPlaceholderCircuitConfig {

	@Value("${feign.json-placeholder.url}")
	private String baseUrl;
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

	/**
	 * Registry.of 를 통해 CircuitBreakerConfig 인스턴스를 주입해주는 방식
	 * 원하는 인스턴스를 선택해서 주입하는 방식
	 * @return
	 */
	@Bean
	public CircuitBreakerRegistry circuitBreakerRegistry() {
		CircuitBreakerRegistry ofCircuitInstance = CircuitBreakerRegistry.of(getCircuitBreakerConfig());
		return ofCircuitInstance;
	}

	/**
	 * CircuitBreakerConfig 인스턴스를 커스텀으로 생성
	 * @return
	 */
	private CircuitBreakerConfig getCircuitBreakerConfig(){
		CircuitBreakerConfig circuitConfig = CircuitBreakerConfig.custom()
			.waitDurationInOpenState(Duration.ofMinutes(5L))
			.build();

		return circuitConfig;
	}
}
