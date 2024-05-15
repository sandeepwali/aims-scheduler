package com.solum.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTempelate<T> {
	
	private String statusMessage;
	private int statusCode;
	private long count;
	List<T> data;

}
