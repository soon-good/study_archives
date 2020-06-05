package com.study.application_event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

public class AppContextEventHandler implements ApplicationListener<ApplicationContextEvent> {

	@Autowired
	ApplicationContext context;

	@Override
	public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
		ApplicationContext applicationContext = applicationContextEvent.getApplicationContext();
		System.out.println("======= AppStartEventHandler :: application started =======");
	}
}
