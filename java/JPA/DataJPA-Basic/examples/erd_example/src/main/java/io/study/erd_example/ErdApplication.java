package io.study.erd_example;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(modifyOnCreate = false)
@SpringBootApplication
public class ErdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErdApplication.class, args);
	}

	// 샘플 예제를 위한 fake 데이터 생성
	// UUID를 이용해 난수를 생성했다.
	// 보통은 SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어내는 편이다.
	// SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어냈을 때 아래의 람다 구문의
	// Optional.of( ... ) 메서드 내에 해당 세션에 대한 요청 ID를 넣어주면 된다.
	@Bean
	public AuditorAware<String> auditorAware(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
