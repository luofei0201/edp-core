package com.zero.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hello world!
 * 
 */
@SpringBootApplication()
public class App  {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(App.class);
		application.addListeners(new AppListener());
		application.run(args);
		//SpringContextUtil.setApplicationContext(app);
	}
}
