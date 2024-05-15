package com.solum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.solum")
@EnableFeignClients
public class SpringBootQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootQuartzApplication.class, args);
	}

}
