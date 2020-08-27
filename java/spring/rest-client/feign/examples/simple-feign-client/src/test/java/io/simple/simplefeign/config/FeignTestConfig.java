package io.simple.simplefeign.config;

import io.simple.simplefeign.api.external.JsonPlaceholderClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@EnableFeignClients(clients = JsonPlaceholderClient.class)
@RestController
@Configuration
@EnableAutoConfiguration
public class FeignTestConfig {

}
