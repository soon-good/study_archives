package io.study.resilience4jfeign1.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LegacyRestClientConfig {

	private final static Logger LOGGER = LoggerFactory.getLogger(LegacyRestClientConfig.class);

	@Bean
	public RestTemplate legacyRestTemplate(){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setReadTimeout(300);
		factory.setConnectionRequestTimeout(300);
		factory.setConnectTimeout(300);

		RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(factory));
		restTemplate.setInterceptors(Collections.singletonList(new InternalLoggingInterceptor()));
		return restTemplate;
	}

	private static class InternalLoggingInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
			ClientHttpRequestExecution execution) throws IOException {

			loggingRequest(httpRequest, bytes);
			ClientHttpResponse response = execution.execute(httpRequest, bytes);
			loggingResponse(httpRequest.getURI(), response);

			return response;
		}

		private void loggingRequest(HttpRequest httpRequest, byte [] body){
			StringBuilder LOG_FORMATTER = new StringBuilder();

			HttpMethod method = httpRequest.getMethod();
			HttpHeaders headers = httpRequest.getHeaders();
			URI uri = httpRequest.getURI();
			String bodyString = new String(body, StandardCharsets.UTF_8);

			LOG_FORMATTER.append("[legacy rest client] >>> ")
				.append("Method :: ").append(method).append(" , ")
				.append("Header :: ").append(headers).append(" , ")
				.append("body :: ").append(bodyString)
				.append("FROM URL :: ").append(uri).append(" , ");

			LOGGER.debug(LOG_FORMATTER.toString());
		}

		private void loggingResponse(URI uri, ClientHttpResponse response) throws IOException {
			StringBuilder LOG_FORMATTER = new StringBuilder();

			HttpStatus statusCode = response.getStatusCode();
			InputStream body = response.getBody();
			String responseBody = StreamUtils.copyToString(body, StandardCharsets.UTF_8);

			LOG_FORMATTER.append("[legacy rest template] ")
				.append("Uri :: ").append(uri)
				.append(", StatusCode :: ").append(statusCode)
				.append(", Response Body ::").append(responseBody);

			LOGGER.debug(LOG_FORMATTER.toString());
		}
	}
}
