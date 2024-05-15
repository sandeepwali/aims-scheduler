package com.solum.entity;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiList {

	private String method;
	private Map<String, String> headers = new HashMap<>();
	private String url;

}
