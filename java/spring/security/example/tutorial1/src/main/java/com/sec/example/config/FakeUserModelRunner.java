package com.sec.example.config;

import com.sec.example.auth.UserAuthService;
import com.sec.example.auth.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class FakeUserModelRunner implements ApplicationRunner {

	@Autowired
	UserAuthService userAuthService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		UserModel userModel = userAuthService.createUser("elephant", "1111");
		System.out.println(userModel.getUsername() + ", " + userModel.getPassword());
	}
}
