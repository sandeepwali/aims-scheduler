package com.solum.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solum.entity.ApiList;
import com.solum.entity.EmailList;
import com.solum.entity.EmailMessageWrapper;
import com.solum.entity.JobDataInput;
import com.solum.entity.JobOutputList;
import com.solum.entity.PostApiWrapper;
import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.JobStatus;
import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.repository.label.RmaRepository;
import com.solum.repository.scheduler.JobStatusRepository;
import com.solum.utility.UtilityMethodsForJob;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisallowConcurrentExecution
public class RmaJob extends QuartzJobBean {

	@Autowired
	private RmaRepository rmaRepository;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private UtilityMethodsForJob utilityMethodsForJob;
	
	@Autowired
	private StreamBridge streamBridgeEmail;
	
	@Autowired
	private StreamBridge streamBridgeLabel;
	
	@Autowired
	private StreamBridge streamBridgePostApi;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		Date startDate = new Date();
		log.info("Region Job Started ");
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		String jobId = jobDataMap.getString("jobId");
		String inputJson = jobDataMap.getString("json");
		String uuid = jobDataMap.getString("uuid");
		String schedulerJobInfoString = jobDataMap.getString("jobInfo");
				
		ObjectMapper mapper = new ObjectMapper();
		JobDataInput jobDataInput = null;
		AtomicReference<SchedulerJobInfo> schedulerJobInfo = new AtomicReference<>();
		try {
			jobDataInput = mapper.readValue(inputJson, JobDataInput.class);
			schedulerJobInfo.set(mapper.readValue(schedulerJobInfoString, SchedulerJobInfo.class));
			
		} catch (JsonProcessingException e) {
			log.error("Error while parsing json into Object");
		}
		String region = jobDataInput.getJobParameters().getAreaValue();
		int thresholdLimit = jobDataInput.getJobParameters().getRmaThreshold();
		List<RmaLabels> rmaLabels = rmaRepository.getRmaLabel(jobDataInput.getJobParameters().getAreaValue(), "RMA");
		int size = rmaLabels.size();
		
		String jobDetailGroup = context.getJobDetail().getKey().getGroup();
		String jobDetailName = context.getJobDetail().getKey().getName();
		String fireTime = context.getFireTime().toInstant().toEpochMilli() + "";
		String jobExecutionId = jobDetailGroup + jobDetailName + fireTime;
		
		if (size >= thresholdLimit) {

			// Creating Excel
			utilityMethodsForJob.createExcel(rmaLabels, jobId);

			// Email Trigger 
			List<JobOutputList> jobOutputList = jobDataInput.getJobOutputList();
			jobOutputList.forEach((j) -> {
				List<EmailList> emailList = j.getEmailList();
				AtomicInteger counter = new AtomicInteger(1);
				emailList.forEach((em) -> {
					EmailMessageWrapper emailWrapper = new EmailMessageWrapper();
					emailWrapper.setRmaLabels(rmaLabels);
					emailWrapper.setJobId(jobId);
					emailWrapper.setEmailList(em);
					emailWrapper.setThreshold(thresholdLimit);
					emailWrapper.setRegion(region);
					emailWrapper.setParentJobId(uuid);
					emailWrapper.setJobHistoryId(jobExecutionId);
					emailWrapper.setSchedulerJobInfo(schedulerJobInfo.get());
					emailWrapper.setEmailJobId(jobExecutionId+"_"+counter.get());
					counter.getAndIncrement();
					streamBridgeEmail.send("email-out-0", emailWrapper);
				});
				counter.set(1);
			});
			
			
			// DeleteLabel Trigger
			rmaLabels.forEach((i) -> {
				streamBridgeLabel.send("deleteLabel-out-0", i);
			});
			
			// Post Api Trigger
			jobOutputList.forEach((a) -> {
				List<ApiList> apiList = a.getApiList();
				
				AtomicInteger counter = new AtomicInteger(1);
				apiList.forEach((api) -> {
					PostApiWrapper postApiWrapper = new PostApiWrapper();
					postApiWrapper.setRmaLabels(rmaLabels);
					postApiWrapper.setApiList(api);
					postApiWrapper.setJobId(jobId);
					postApiWrapper.setParentJobId(uuid);
					postApiWrapper.setJobHistoryId(jobExecutionId);
					postApiWrapper.setSchedulerJobInfo(schedulerJobInfo.get());
					postApiWrapper.setApiJobId(jobExecutionId+"_"+counter.get());
					counter.getAndIncrement();
					streamBridgePostApi.send("postapi-out-0", postApiWrapper);
				});
				counter.set(1);
			});
			
			
			// Save report into DB
			String jsonStringOfLabels = "";
			try {
				jsonStringOfLabels = mapper.writeValueAsString(rmaLabels);
				// List<RmaLabels> participantJsonList = mapper.readValue(json, new
				// TypeReference<List<RmaLabels>>(){});
			} catch (JsonProcessingException e) {
				log.error("Error while converting Labels in Json String");
			}

			JobStatus jobStatus = new JobStatus();
			
			jobStatus.setJobId(jobExecutionId);
			
			// Job data input
			jobStatus.setJobDataInput(inputJson);
			
			// JobData
			jobStatus.setData(jsonStringOfLabels);

			// jobStatus.set
			jobStatus.setJobExectcutionStatus(true);
			
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
			
			// Start Date
			jobStatus.setStartDateInLocal(startDate);
			
			//End Date
			Date endDate = new Date();
			jobStatus.setEndDateInLocal(endDate);
			
			jobStatus.setParentJobId(uuid);
			
			jobStatusRepository.save(jobStatus);
		}

		
		log.info("Region Job Finished....");
	}
}
