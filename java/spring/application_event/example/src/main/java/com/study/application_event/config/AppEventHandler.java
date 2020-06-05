package com.study.application_event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class AppEventHandler implements ApplicationListener<ApplicationEvent> {

	@Autowired
	ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		Object source = event.getSource();
		System.out.println("======= application started =======");
	}
}
