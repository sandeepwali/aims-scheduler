package com.solum.job;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;

//@Configuration
public class JobSupportDeleteLabelError {

	@Bean
	public Consumer<String> errordeletelabel() {
		return (msg) -> {
			System.out.println("Consuming labels into DeleteLabelError:" + msg);
		};
	}
}
