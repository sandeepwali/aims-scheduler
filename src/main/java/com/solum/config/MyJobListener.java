package com.solum.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.solum.entity.scheduler.JobStatus;
import com.solum.repository.scheduler.JobStatusRepository;

public class MyJobListener implements JobListener {

	@Autowired
	private JobStatusRepository jobStatusRepository;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Aims Scheduler";
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {

			if(jobException !=null) {
		
			JobDataMap jobDataMap = context.getMergedJobDataMap();
		
			JobStatus jobStatus = new JobStatus();
			String jobDetailGroup = context.getJobDetail().getKey().getGroup();
			String jobDetailName = context.getJobDetail().getKey().getName();
			String fireTime = context.getFireTime().toInstant().toEpochMilli() + "";
			String jobExecutionId = jobDetailGroup + jobDetailName + fireTime;
			jobStatus.setJobId(jobExecutionId);
			String uuid = jobDataMap.getString("uuid");
			
			// Parent Job id
			jobStatus.setParentJobId(uuid);
			
			// jobStatus.set
			jobStatus.setJobExectcutionStatus(false);
			
			// Job Name
			String jobName = context.getJobDetail().getKey().getName();
			jobStatus.setJobName(jobName);
			
			// Job Group
			String jobGroup = context.getJobDetail().getKey().getGroup();
			jobStatus.setJobGroup(jobGroup);
			
			// Fire time
			long jobFireTime = context.getFireTime().toInstant().toEpochMilli();
			jobStatus.setFireTime(jobFireTime);
			
			Trigger trigger = context.getTrigger();
			
			// Trigger Name
			String triggerName = trigger.getKey().getName();
			jobStatus.setTriggerName(triggerName);
			
			// Trigger group
			String triggerGroup = trigger.getKey().getGroup();
			jobStatus.setTriggerGroup(triggerGroup);
			
			// Trigger Type
			String triggerType = trigger.getClass().toString();
			jobStatus.setTriggerType(triggerType);
			
			// TimeZone
			TimeZone timeZone =(TimeZone) jobDataMap.get("timeZone");
			jobStatus.setTimeZone(timeZone);
			
			// UTC Time 
			TimeZone timeZoneUtc = TimeZone.getTimeZone("UTC");
			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss"); 
			dateformat.setTimeZone(timeZoneUtc);
			SimpleDateFormat dateFormatSecond = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss"); 
			Date utcDate=null;
			try {
				utcDate = dateFormatSecond.parse(dateformat.format(new Date()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jobStatus.setUtcDate(utcDate);
			System.out.println(utcDate);
			
			// Start Date
			// We don't have access here
			
			//End Date
			Date endDate = new Date();
			jobStatus.setEndDateInLocal(endDate);
			System.out.println(jobStatus);
			jobStatusRepository.save(jobStatus);
		}
	}
}
