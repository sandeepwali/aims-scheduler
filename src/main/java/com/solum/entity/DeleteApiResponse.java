package com.solum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteApiResponse {

	private String returnMsg;
	private String returnCode;
	private String customBatchId;
	
//	    "returnMsg": "It has completed the request.",
//	    "returnCode": "200 OK",
//	    "customBatchId": "20220302122029043929"
//	

	
}
