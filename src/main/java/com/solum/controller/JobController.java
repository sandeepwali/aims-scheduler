package com.solum.controller;

import static org.quartz.CronExpression.isValidExpression;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solum.entity.CreateJobDetail;
import com.solum.entity.EmailList;
import com.solum.entity.Group;
import com.solum.entity.JobDataInput;
import com.solum.entity.JobOutputList;
import com.solum.entity.JobStatusDto;
import com.solum.entity.Message;
import com.solum.entity.ResponseTempelate;
import com.solum.entity.SchedulerJobInfoDto;
import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.feign.AimsCoreDbServiceProxy;
import com.solum.repository.label.RmaRepository;
import com.solum.repository.scheduler.JobStatusRepository;
import com.solum.service.SchedulerJobService;
import com.solum.utility.EmailUtility;
import com.solum.utility.UtilityMethodsForJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")

@OpenAPIDefinition(info = 
	@Info(
		title = "aims-scheduler", 
		description = "Article Bulk Upload"
		), 
	security = @SecurityRequirement(name = "api-key")
)

@SecurityScheme(type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, name = "api-key")

public class JobController {

	private final SchedulerJobService scheduleJobService;

	@Autowired
	private RmaRepository rmaRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private AimsCoreDbServiceProxy aimsCoreDbServiceProxy;

	@Autowired
	private UtilityMethodsForJob utilityMethodsForJob;

	@Autowired
	private EmailUtility emailUtility;

	// @GetMapping("/email/{name}")
	// public boolean sendMessage(@PathVariable("name") String msg) {
	// boolean sendErrorEmail = deleteLabelJobSupport.sendErrorDeleteLabel(msg);
	// boolean sendErrorEmail1 = jobEmailSupport.sendErrorEmail(msg);
	// return sendErrorEmail;
	// } // jobs/group/RMAJOB/name/RmaLabelsByThressholdJobRegionHessen'
	

	@GetMapping("job-group")
	public ResponseEntity<?> getAllJobGroups() {
		List<String> jobGroupList = scheduleJobService.getAllJobGroups();
		ResponseTempelate<String> response = new ResponseTempelate<>();
		response.setCount(jobGroupList.size());
		response.setStatusCode(200);
		response.setStatusMessage("Group fetched sucessfully..");
		response.setData(jobGroupList);
		return ResponseEntity.ok(response);
	}

	@GetMapping("job-group/{job-group}")
	public ResponseEntity<?> getAllJobByGroup(@PathVariable("job-group") String jobGroup, Pageable pageable) {
		List<SchedulerJobInfoDto> jobList = scheduleJobService.getAllJobListByGroup(jobGroup, pageable);
		ResponseTempelate<SchedulerJobInfoDto> response = new ResponseTempelate<>();
		response.setCount(jobList.size());
		response.setStatusCode(200);
		response.setStatusMessage("GroupJob fetched sucessfully..");
		response.setData(jobList);
		return ResponseEntity.ok(response);
	}

	@GetMapping("job-group/{job-group}/name/{job-name}")
	public ResponseEntity<?> getAllJobByGroupAndJobName(@PathVariable("job-group") String jobGroup,
			@PathVariable("job-name") String jobName, Pageable pageable) {
		List<SchedulerJobInfoDto> allJobListByGroupAndJobName = scheduleJobService
				.getAllJobListByGroupAndJobName(jobGroup, jobName, pageable);
		ResponseTempelate<SchedulerJobInfoDto> response = new ResponseTempelate<>();
		response.setCount(allJobListByGroupAndJobName.size());
		response.setStatusCode(200);
		response.setStatusMessage("GroupJob with name fetched sucessfully..");
		response.setData(allJobListByGroupAndJobName);
		return ResponseEntity.ok(response);
	}

	@GetMapping("job-group/{job-group}/name/{job-name}/history")
	public ResponseEntity<?> getAllJobByGroupAndJobNameHistory(@PathVariable("job-group") String jobGroup,
			@PathVariable("job-name") String jobName,
			Pageable pageable,
			@RequestHeader(name = "includeData", defaultValue = "false", required = false) boolean includeData,
			@RequestParam(name = "jobId", required = false) String jobId) {
		List<JobStatusDto> jobRunHistoryList = null;
		if (jobId == null) {
			jobRunHistoryList = scheduleJobService.getAllJobListByGroupAndJobNameHistory(jobGroup, jobName, pageable);
		} else {
			jobRunHistoryList = scheduleJobService.getAllJobListByGroupAndJobNameHistory(jobGroup, jobName, jobId,
					pageable);
		}
		if (!includeData) {
			jobRunHistoryList = jobRunHistoryList.stream().map(job -> {
				job.setData(null);
				return job;
			}).collect(Collectors.toList());
		}
		ResponseTempelate<JobStatusDto> response = new ResponseTempelate<>();
		response.setCount(jobRunHistoryList.size());
		response.setStatusCode(200);
		response.setStatusMessage("History of GroupJob with Name fetched sucessfully..");
		response.setData(jobRunHistoryList);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/getAllJobs")
	public Object getAllJobs() throws SchedulerException {
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		return jobList;
	}

	@PostMapping("/jobs/group/{job-group}/name/{job-name}")
	public ResponseEntity<?> creatNewJob(@RequestBody CreateJobDetail createJobDetail, 
			@PathVariable("job-group") String jobGroup, @PathVariable("job-name") String jobName){
		log.debug("Request for create job is received having name :{} and group :{}", jobName, jobGroup);
		ResponseTempelate<SchedulerJobInfo> rt = new ResponseTempelate<>();
		TimeZone timeZone=null;
		ObjectMapper mapper = new ObjectMapper();
		String jobDataInputString="";
		try {
			ZoneId zoneId = ZoneId.of(createJobDetail.getZone());
			timeZone = TimeZone.getTimeZone(zoneId);
			jobDataInputString = mapper.writeValueAsString(createJobDetail.getJobDataInput());
		} catch (Exception e) {
			rt.setCount(0);
			rt.setStatusCode(400);
			rt.setStatusMessage("Bad Request, zone id is incorrect");
			rt.setData(null);
			return ResponseEntity.badRequest().body(rt);
		}
		SchedulerJobInfo jobInfo = new SchedulerJobInfo();
		jobInfo.setJobName(jobName);
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setCronExpression(createJobDetail.getCronExpression());
		jobInfo.setDescription(createJobDetail.getDescription());
		jobInfo.setZone(timeZone);
		jobInfo.setJobDataInput(jobDataInputString);
		Message msg = (Message)saveOrUpdate(jobInfo);
		if(msg.isValid()) {
			rt.setCount(1);
			rt.setStatusCode(200);
			rt.setStatusMessage("Job created successfully!");
			SchedulerJobInfo savedJob = scheduleJobService.getJobByName(jobName);
			rt.setData(Arrays.asList(savedJob));
			return ResponseEntity.ok().body(rt);
		}else {
			if(msg.getStatusCode() == 409) {
				SchedulerJobInfo savedJob = scheduleJobService.getJobByName(jobName);
				rt.setData(Arrays.asList(savedJob));
				rt.setCount(1);
				rt.setStatusCode(409);
				rt.setStatusMessage(msg.getMsg());
				return ResponseEntity.badRequest().body(rt);
			}
			rt.setCount(0);
			rt.setStatusCode(400);
			rt.setStatusMessage(msg.getMsg());
			rt.setData(null);
			return ResponseEntity.badRequest().body(rt);
		}
	}
	
	@PutMapping("/jobs/group/{job-group}/name/{job-name}")
	public ResponseEntity<?> updateJob(@RequestBody CreateJobDetail createJobDetail, 
			@PathVariable("job-group") String jobGroup, @PathVariable("job-name") String jobName){
		log.debug("Request for update job is received having name :{} and group :{}", jobName, jobGroup);
		ResponseTempelate<SchedulerJobInfo> rt = new ResponseTempelate<>();
		SchedulerJobInfo existedJobInfo = scheduleJobService.getJobByName(jobName);
		
		if(existedJobInfo == null) {
			rt.setCount(0);
			rt.setStatusCode(400);
			rt.setStatusMessage("Job is not exists, please provide valid jobId");
			rt.setData(null);
			return ResponseEntity.badRequest().body(rt);
		}
		TimeZone timeZone=null;
		ObjectMapper mapper = new ObjectMapper();
		String jobDataInputString="";
		try {
			ZoneId zoneId = ZoneId.of(createJobDetail.getZone());
			timeZone = TimeZone.getTimeZone(zoneId);
			jobDataInputString = mapper.writeValueAsString(createJobDetail.getJobDataInput());
		} catch (Exception e) {
			rt.setCount(0);
			rt.setStatusCode(400);
			rt.setStatusMessage("Bad Request, zone id is incorrect");
			rt.setData(null);
			return ResponseEntity.badRequest().body(rt);
		}
		SchedulerJobInfo jobInfo = new SchedulerJobInfo();
		jobInfo.setJobName(jobName);
		jobInfo.setJobGroup(jobGroup);
		jobInfo.setCronExpression(createJobDetail.getCronExpression());
		jobInfo.setDescription(createJobDetail.getDescription());
		jobInfo.setZone(timeZone);
		jobInfo.setJobDataInput(jobDataInputString);
		jobInfo.setJobId(existedJobInfo.getJobId());
		Message msg = (Message)saveOrUpdate(jobInfo);
		if(msg.isValid()) {
			rt.setCount(1);
			rt.setStatusCode(200);
			rt.setStatusMessage("Job updated successfully!");
			SchedulerJobInfo savedJob = scheduleJobService.getJobByName(jobName);
			rt.setData(Arrays.asList(savedJob));
			return ResponseEntity.ok().body(rt);
		}else {
			if(msg.getStatusCode() == 409) {
				SchedulerJobInfo savedJob = scheduleJobService.getJobByName(jobName);
				rt.setData(Arrays.asList(savedJob));
				rt.setCount(1);
				rt.setStatusCode(409);
				rt.setStatusMessage(msg.getMsg());
				return ResponseEntity.badRequest().body(rt);
			}
			rt.setCount(0);
			rt.setStatusCode(400);
			rt.setStatusMessage(msg.getMsg());
			rt.setData(null);
			return ResponseEntity.badRequest().body(rt);
		}
	}
	
	@RequestMapping(value = "/saveOrUpdate", method = { RequestMethod.POST })
	public Object saveOrUpdate(SchedulerJobInfo job) {
		log.info("params, job = {}", job);
		Message message = Message.failure();
		
		String jobDataInput = job.getJobDataInput();
		ObjectMapper mapper = new ObjectMapper();
		JobDataInput dataInput = null;
		try {
			dataInput = mapper.readValue(jobDataInput, JobDataInput.class);
		} catch (JsonProcessingException e) {
			message.setMsg("Error into parsing Json, please provie a valid Json");
			return message;
		}

		AtomicBoolean emailValidation = new AtomicBoolean(true);
		List<JobOutputList> jobOutputList = dataInput.getJobOutputList();
		jobOutputList.forEach((j) -> {
			List<EmailList> emailList = j.getEmailList();
			emailList.forEach((email) -> {
				List<String> toList = email.getTo();
				List<String> ccList = email.getCc();
				if (toList != null) {
					boolean validateEmails = emailUtility.validateEmails(toList);
					if (!validateEmails) {
						emailValidation.getAndSet(false);
					}
				}
				if (ccList != null) {
					boolean validateEmails = emailUtility.validateEmails(ccList);
					if (!validateEmails) {
						emailValidation.getAndSet(false);
					}
				}

			});

		});

		if (!emailValidation.get()) {
			message.setMsg("Provided emails in Job Data input is not valid please check and proivde valid emails");
			return message;
		}

		String jobGroup = job.getJobGroup();
		boolean validExpression = isValidExpression(job.getCronExpression());
		if (!validExpression) {
			message.setMsg("Cron expression is not valid, please provide a valid Cron Expression");
			return message;
		}

		if (jobGroup.equals(Group.RMAJOB.toString()) || jobGroup.equals(Group.STOREJOB.toString())) {
			try {
				boolean status = scheduleJobService.saveOrupdate(job);
				if (status) {
					message = Message.success();
				} else {
					Message.failure();
					message.setMsg("Job is already exist, hence unable to create new Job with same name");
					message.setStatusCode(409);
				}
			} catch (Exception e) {
				message.setMsg(e.getMessage());
				log.error("updateCron ex:", e);
			}
			return message;
		} else {
			message.setMsg("Please provide a valid Job Group");
			return message;
		}
	}

	@GetMapping("/metaData")
	public Object metaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduleJobService.getMetaData();
		return metaData;
	}

	@RequestMapping(value = "/runJob", method = { RequestMethod.POST })
	public Object runJob(SchedulerJobInfo job) {
		log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.startJobNow(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("runJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/pauseJob", method = { RequestMethod.POST })
	public Object pauseJob(SchedulerJobInfo job, String jobName) {
		System.out.println("JobName from controller" + jobName);
		log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.pauseJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("pauseJob ex:", e);
		}
		return message;
	}

	@RequestMapping(value = "/resumeJob", method = { RequestMethod.POST })
	public Object resumeJob(SchedulerJobInfo job) {
		log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.resumeJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("resumeJob ex:", e);
		}
		return message;
	}
	
	@DeleteMapping("/jobs/group/{job-group}/name/{job-name}")
	public ResponseEntity<?> deleteJobApi(@PathVariable("job-group") String jobGroup, @PathVariable("job-name") String jobName){
		log.debug("Request for delete Job having name :{} is received", jobName);
		SchedulerJobInfo jobByName = scheduleJobService.getJobByName(jobName);
		ResponseTempelate<String> rt = new ResponseTempelate<String>();
		if(jobByName == null) {
			rt.setCount(0);
			rt.setData(null);
			rt.setStatusCode(400);
			rt.setStatusMessage("Job doesn't already exsist.. Please check and provide valid job name");
			return ResponseEntity.badRequest().body(rt);
		}
		Message msg = (Message)deleteJob(jobByName);
		if(msg.isValid()) {
			rt.setCount(1);
			rt.setStatusCode(200);
			rt.setStatusMessage("Job deleted successfully!");
			rt.setData(null);
			return ResponseEntity.ok().body(rt);
		}else {
			rt.setCount(0);
			rt.setStatusCode(500);
			rt.setStatusMessage(msg.getMsg());
			rt.setData(null);
			return ResponseEntity.internalServerError().body(rt);
		}
	}

	@RequestMapping(value = "/deleteJob", method = { RequestMethod.POST })
	public Object deleteJob(SchedulerJobInfo job) {
		log.info("params, job = {}", job);
		Message message = Message.failure();
		try {
			scheduleJobService.deleteJob(job);
			message = Message.success();
		} catch (Exception e) {
			message.setMsg(e.getMessage());
			log.error("deleteJob ex:", e);
		}
		return message;
	}
}
