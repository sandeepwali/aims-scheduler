package com.solum.entity;

import java.util.List;

import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.SchedulerJobInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostApiWrapper {

	private ApiList apiList;
	private List<RmaLabels> rmaLabels;
	private String jobId;
	private String parentJobId;
	private String jobHistoryId;
	private String apiJobId;
	private SchedulerJobInfo schedulerJobInfo;
	
	
}
