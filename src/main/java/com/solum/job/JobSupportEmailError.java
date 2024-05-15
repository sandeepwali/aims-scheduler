package com.solum.job;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;

//@Configuration
public class JobSupportEmailError {

	@Bean
	public Consumer<String> erroremail() {
		return (msg) -> {
			System.out.println("Consuming email Data into email error: " + msg);
		};
	}
	
}
