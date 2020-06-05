package com.study.application_event;

import com.study.application_event.config.AppContextRefreshedEventHandler;
import com.study.application_event.config.AppEventHandler;
import com.study.application_event.config.AppContextEventHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
//		run.addApplicationListener(new AppEventHandler());
//		run.addApplicationListener(new AppContextEventHandler());
//		run.addApplicationListener(new AppContextRefreshedEventHandler());
	}

}
