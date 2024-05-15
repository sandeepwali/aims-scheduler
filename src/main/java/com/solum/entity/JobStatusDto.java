package com.solum.entity;

import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.solum.entity.label.RmaLabels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobStatusDto {

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
	private List<RmaLabels> data;
}
