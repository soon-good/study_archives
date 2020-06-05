package com.study.application_event.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ContextRefreshListner {

	@Autowired
	ApplicationContext context;

	@EventListener({ContextRefreshedEvent.class})
	void contextRefreshed(){
		System.out.println(">>>>>>> context refreshed ======= ");
	}

	@EventListener({ApplicationEvent.class})
	void applicationListener(){
		System.out.println(">>>>>>> application listener ======= ");
	}

	@EventListener({ApplicationStartedEvent.class})
	void applicationStarted(){
		System.out.println(">>>>>>> application started ======= ");
	}
}
