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
			.logLevel(Logger.Level.BASIC)
			.target(JsonPlaceholderClient.class, "https://jsonplaceholder.typicode.com");
	}
}
