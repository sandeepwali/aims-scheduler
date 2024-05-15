package com.solum.entity.scheduler;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ApiHistory")
public class ApiHistory {
	
	@Id
	private String jobId;
	private String parentJobId;
	private String jobHistoryId;
	private String jobName;
	private String jobGroup;
	private String jobStatus;
	private String jobClass;
	@Column(columnDefinition="text")
	private String message;
	private long amqpDeliveryTag;
	@Column(columnDefinition="text")
	private String api;
		
}
