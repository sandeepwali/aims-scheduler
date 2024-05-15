package com.solum.service;

import java.util.List;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.solum.entity.JobStatusDto;
import com.solum.entity.SchedulerJobInfoDto;
import com.solum.entity.scheduler.SchedulerJobInfo;

@Service
public interface SchedulerJobService {
	
	public SchedulerMetaData getMetaData() throws SchedulerException;
	public List<SchedulerJobInfo> getAllJobList();
	public boolean deleteJob(SchedulerJobInfo jobInfo);
	public boolean pauseJob(SchedulerJobInfo jobInfo);
	public boolean resumeJob(SchedulerJobInfo jobInfo);
	public boolean startJobNow(SchedulerJobInfo jobInfo);
	public boolean saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception;
	public boolean scheduleNewJob(SchedulerJobInfo jobInfo);
	public void updateScheduleJob(SchedulerJobInfo jobInfo);
	public boolean stopJob(SchedulerJobInfo jobInfo);
	public List<String> getAllJobGroups();
	public List<SchedulerJobInfoDto> getAllJobListByGroup(String groupName, Pageable pageable);
	public List<SchedulerJobInfoDto> getAllJobListByGroupAndJobName(String jobGroup, String jobName, Pageable pageable);
	public List<JobStatusDto> getAllJobListByGroupAndJobNameHistory(String jobGroup, String jobName, Pageable pageable);
	public List<JobStatusDto> getAllJobListByGroupAndJobNameHistory(String jobGroup, String jobName, String jobId,
			Pageable pageable);
	public SchedulerJobInfo getJobByName(String jobName);
	public SchedulerJobInfo getJobById(String jobId);
	
	
}
