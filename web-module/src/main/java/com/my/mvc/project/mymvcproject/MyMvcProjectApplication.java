package com.my.mvc.project.mymvcproject;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAdminServer
@EnableCaching
@EnableAsync
public class MyMvcProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyMvcProjectApplication.class, args);
	}

}
