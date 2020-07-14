package com.study.sec1;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	public MySecurityConfig(UserDetailsService userDetailsService){
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
//		super.configure(http);
		http.authorizeRequests()
			.anyRequest().authenticated();

		http.formLogin();

		http.logout()
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login")
			.addLogoutHandler(new LogoutHandler() {
				@Override
				public void logout(	HttpServletRequest httpServletRequest,
									HttpServletResponse httpServletResponse,
									Authentication authentication) {
					HttpSession session = httpServletRequest.getSession();
					session.invalidate();
				}
			})
			.logoutSuccessHandler(new LogoutSuccessHandler() {
				@Override
				public void onLogoutSuccess( HttpServletRequest httpServletRequest,
											 HttpServletResponse httpServletResponse,
											 Authentication authentication) throws IOException, ServletException {
					httpServletResponse.sendRedirect("/login");
				}
			})
			.deleteCookies("remember-me");

		http.rememberMe()
			.rememberMeParameter("remember-me")			// 기본 파라미터 명은 "remember-me"
			.tokenValiditySeconds(3600)					// 로그인 유지시간을 1시간으로 설정 (Default 세팅은 14일)
			.alwaysRemember(false)						// rememberMe 기능이 활성화되지 않아도 항상 실행할까요? false
			.userDetailsService(userDetailsService);	// userDetailService 만들어놓은게 있으면 결합해준다.
	}
}
