package com.solum.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @Builder
public class FeignResponse {
	private Integer statusCode;
	private Boolean success;
	private String errorMessage;
}
