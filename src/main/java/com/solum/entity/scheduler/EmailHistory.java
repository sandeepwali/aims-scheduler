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
@Table(name = "EmailHistory")
public class EmailHistory {
	
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
	private String email;
		
}
