package com.solum.utility;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solum.entity.JobStatusDto;
import com.solum.entity.SchedulerJobInfoDto;
import com.solum.entity.label.RmaLabels;
import com.solum.entity.scheduler.JobStatus;
import com.solum.entity.scheduler.SchedulerJobInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DtoUtility {

	public SchedulerJobInfoDto convertToSchedulerJobInfoDto(SchedulerJobInfo schedulerJobInfo) {
		SchedulerJobInfoDto schedulerJobInfoDto = new SchedulerJobInfoDto();
			schedulerJobInfoDto.setJobId(schedulerJobInfo.getJobId());
			schedulerJobInfoDto.setJobName(schedulerJobInfo.getJobName());
			schedulerJobInfoDto.setJobGroup(schedulerJobInfo.getJobGroup());
			schedulerJobInfoDto.setJobStatus(schedulerJobInfo.getJobStatus());
			schedulerJobInfoDto.setJobClass(schedulerJobInfo.getJobClass());
			schedulerJobInfoDto.setCronExpression(schedulerJobInfo.getCronExpression());
			schedulerJobInfoDto.setDescription(schedulerJobInfo.getDescription());
			schedulerJobInfoDto.setInterfaceName(schedulerJobInfo.getInterfaceName());
			schedulerJobInfoDto.setRepeatTime(schedulerJobInfo.getRepeatTime());
			schedulerJobInfoDto.setCronJob(schedulerJobInfo.getCronJob());
			schedulerJobInfoDto.setZone(schedulerJobInfo.getZone());
			
//			ObjectMapper mapper = new ObjectMapper();
//			JobDataInput jobDataInput=null;
//			try {
//				jobDataInput = mapper.readValue(schedulerJobInfo.getJobDataInput(), JobDataInput.class);
//			} catch (JsonProcessingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				log.error("Error while parsing Json");
//			}
			//schedulerJobInfoDto.setJobDataInput(jobDataInput);
		return schedulerJobInfoDto;
	}
	
	public JobStatusDto convertToJobStatusDto(JobStatus jobStatus) {
		JobStatusDto jobStatusDto = new JobStatusDto();
		jobStatusDto.setJobId(jobStatus.getJobId());
		jobStatusDto.setJobName(jobStatus.getJobName());
		jobStatusDto.setJobGroup(jobStatus.getJobGroup());
		jobStatusDto.setFireTime(jobStatus.getFireTime());
		jobStatusDto.setTriggerName(jobStatus.getTriggerName());
		jobStatusDto.setTriggerGroup(jobStatus.getTriggerGroup());
		jobStatusDto.setTriggerType(jobStatus.getTriggerType());
		jobStatusDto.setJobExectcutionStatus(jobStatus.isJobExectcutionStatus());
		jobStatusDto.setStartDateInLocal(jobStatus.getStartDateInLocal());
		jobStatusDto.setEndDateInLocal(jobStatus.getEndDateInLocal());
		jobStatusDto.setUtcDate(jobStatus.getUtcDate());
		jobStatusDto.setTimeZone(jobStatus.getTimeZone());

		ObjectMapper mapper = new ObjectMapper();
		String rmaLabels = jobStatus.getData();
		List<RmaLabels> rmaLabelList=null;
		if(rmaLabels != null) {
		try {
			rmaLabelList = mapper.readValue(rmaLabels, new TypeReference<List<RmaLabels>>(){});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			log.error("Error while parsing RMA lable json");
		}
		}
		jobStatusDto.setData(rmaLabelList);
		return jobStatusDto;
	
}
}
