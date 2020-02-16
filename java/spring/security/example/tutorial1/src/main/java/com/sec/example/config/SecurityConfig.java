package com.sec.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests() 	// http 요청을 Authorizing 하겠다.
				// Authorizing 할때 AntMatcher로 /, /welcome은 허용하겠다.
				.antMatchers("/", "/welcome").permitAll()
				// 그 외의 어떤 요청이든 Authorizing(인증)을 하겠다.
				.anyRequest().authenticated()
			.and() // 세부설정을 하고 난 후 다시 HttpSecurity 객체를 받아오기 위한 and 구문
				.formLogin()
			.and() // 세부설정을 하고 난 후 다시 HttpSecurity 객체를 받아오기 위한 and 구문
				.httpBasic();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
