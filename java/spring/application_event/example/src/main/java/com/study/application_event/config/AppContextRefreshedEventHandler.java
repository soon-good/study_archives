package com.study.application_event.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class AppContextRefreshedEventHandler implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		System.out.println("======= AppContextRefreshedEventHandler:: =======");
	}
}
