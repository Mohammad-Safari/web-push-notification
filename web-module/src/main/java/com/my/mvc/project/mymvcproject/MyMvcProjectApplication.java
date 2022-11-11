package com.my.mvc.project.mymvcproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class MyMvcProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMvcProjectApplication.class, args);
	}

}
