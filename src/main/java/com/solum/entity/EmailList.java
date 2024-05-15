package com.solum.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailList {

	@JsonProperty("to") 
	private List<String> to;
	private List<String> cc;
	private String body;
	
}
