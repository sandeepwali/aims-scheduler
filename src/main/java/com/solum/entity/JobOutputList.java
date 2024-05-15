package com.solum.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOutputList {

	private List<EmailList> emailList;
	private List<ApiList> apiList;
	
}
