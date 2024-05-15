package com.solum.entity.scheduler;

import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "jobhistory")
public class JobStatus {

	@Id
	private String jobId;
	private String jobName;
	private String jobGroup;
	private long fireTime;
	private String triggerName;
	private String triggerGroup;
	private String triggerType;
	private boolean jobExectcutionStatus;
	private Date startDateInLocal;
	private Date endDateInLocal;
	private Date utcDate;
	private TimeZone timeZone;
	@Column(columnDefinition="text")
	private String data;
	@Column(columnDefinition="text")
	private String jobDataInput;
	private String parentJobId;
	

	
	
}
