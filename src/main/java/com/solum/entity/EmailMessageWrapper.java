package com.solum.entity;

import java.util.List;

import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.SchedulerJobInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageWrapper {

	private String jobId;
	private List<RmaLabels> rmaLabels;
	private EmailList emailList;
	private String region;
	private int threshold;
	private String parentJobId;
	private String jobHistoryId;
	private String emailJobId;
	private SchedulerJobInfo schedulerJobInfo;
	
}
