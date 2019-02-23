package com.zero.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

public class AppListener implements ApplicationListener<ApplicationReadyEvent> {
	private Logger logger = LoggerFactory.getLogger(AppListener.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		logger.info("应用程序准备就绪");
	}
}
