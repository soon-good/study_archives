package io.study.resilience4jfeign1.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitMonitorConfig {

	@Bean
	public PrometheusMeterRegistry circuitMeterRegistry(){
		return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	}

	@Bean
	public TaggedCircuitBreakerMetrics taggedCircuitBreakerMetrics(CircuitBreakerRegistry circuitBreakerRegistry){

		// MeterRegistry 들 중에서 가장 간단한 형태인 SimpleMeterRegistry 타입의 인스턴스를 생성한다.
		MeterRegistry meterRegistry = new SimpleMeterRegistry();

		// FeignRestCircuitConfig 에서 등록한 Bean인 circuitBreakerRegistry 를 등록해주고
		TaggedCircuitBreakerMetrics taggedCircuitBreakerMetrics
			= TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(circuitBreakerRegistry);

		// 위에서 SimpleMeterRegistry 로 선언한 meterRegistry 를 taggedCircuitBreakerMetrics 에 등록한다.
		taggedCircuitBreakerMetrics.bindTo(meterRegistry);

		return taggedCircuitBreakerMetrics;

	}
}