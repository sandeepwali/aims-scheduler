package com.solum.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.solum.component.JobScheduleCreator;
import com.solum.entity.Group;
import com.solum.entity.JobStatusDto;
import com.solum.entity.SchedulerJobInfoDto;
import com.solum.entity.scheduler.JobStatus;
import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.job.RmaJob;
import com.solum.job.SampleStoreJob;
import com.solum.job.SimpleJob;
import com.solum.repository.scheduler.ApiHistoryRepository;
import com.solum.repository.scheduler.JobStatusRepository;
import com.solum.repository.scheduler.SchedulerRepository;
import com.solum.utility.DtoUtility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@Service
public class SchedulerJobServiceImpl implements SchedulerJobService {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private SchedulerRepository schedulerRepository;
	
	@Autowired
	private JobStatusRepository jobStatusRepository;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private JobScheduleCreator scheduleCreator;
	
	@Autowired
	private DtoUtility dtoUtility;
	
	@Autowired
	private ApiHistoryRepository apiHistoryRepository;
	
	@Override
	public SchedulerMetaData getMetaData() throws SchedulerException {
		SchedulerMetaData metaData = scheduler.getMetaData();
		return metaData;
	}

	@Override
	public List<SchedulerJobInfo> getAllJobList() {
		List<SchedulerJobInfo> findAll = schedulerRepository.findAll();
		return findAll;
	}
	
	@Override
	public List<SchedulerJobInfoDto> getAllJobListByGroup(String groupName, Pageable pageable) {
		 List<SchedulerJobInfo> jobListWithGroup = schedulerRepository.findByGroupName(groupName, pageable);
		 List<SchedulerJobInfoDto> jobListWithGroupDto = new ArrayList<>();
		 jobListWithGroup.forEach((j) -> {
			 SchedulerJobInfoDto schedulerJobInfoDto = dtoUtility.convertToSchedulerJobInfoDto(j);
			 jobListWithGroupDto.add(schedulerJobInfoDto); 
		 });
		return jobListWithGroupDto;
	}
	

	@Override
	public boolean deleteJob(SchedulerJobInfo jobInfo) {
		try {
			SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
			schedulerRepository.delete(getJobInfo);
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " deleted.");
			return schedulerFactoryBean.getScheduler()
					.deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
		} catch (SchedulerException e) {
			log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	@Override
	public boolean pauseJob(SchedulerJobInfo jobInfo) {
		try {
			SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
			getJobInfo.setJobStatus("PAUSED");
			schedulerRepository.save(getJobInfo);
			schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused.");
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	@Override
	public boolean stopJob(SchedulerJobInfo jobInfo) {
		try {
			SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
			getJobInfo.setJobStatus("STOPPED");
			schedulerRepository.save(getJobInfo);
			schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
//	            schedulerFactoryBean.getScheduler()getJobInfo;
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused.");
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	@Override
	public boolean resumeJob(SchedulerJobInfo jobInfo) {
		try {
			SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
			getJobInfo.setJobStatus("RESUMED");
			schedulerRepository.save(getJobInfo);
			schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " resumed.");
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to resume job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	@Override
	public boolean startJobNow(SchedulerJobInfo jobInfo) {
		try {
			SchedulerJobInfo getJobInfo = schedulerRepository.findByJobName(jobInfo.getJobName());
			getJobInfo.setJobStatus("SCHEDULED & STARTED");
			schedulerRepository.save(getJobInfo);
			schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled and started now.");
			return true;
		} catch (SchedulerException e) {
			log.error("Failed to start new job - {}", jobInfo.getJobName(), e);
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean saveOrupdate(SchedulerJobInfo scheduleJob) throws Exception {
		boolean status = true;
		if (scheduleJob.getCronExpression().length() > 0) {
			scheduleJob.setCronJob(true);
			String jobGroup = scheduleJob.getJobGroup();
			if (jobGroup.equals(Group.RMAJOB.toString())) {
				scheduleJob.setJobClass(RmaJob.class.getName());
			} else {
				scheduleJob.setJobClass(SampleStoreJob.class.getName());
			}

		} else {
			scheduleJob.setJobClass(SimpleJob.class.getName());
			scheduleJob.setCronJob(false);
			scheduleJob.setRepeatTime((long) 1);
		}
		if (StringUtils.isEmpty(scheduleJob.getJobId())) {
			log.info("Job Info: {}", scheduleJob);
			status = scheduleNewJob(scheduleJob);
		} else {
			updateScheduleJob(scheduleJob);
		}
		scheduleJob.setDescription("i am job number " + scheduleJob.getJobId());
		scheduleJob.setInterfaceName("interface_" + scheduleJob.getJobId());
		log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
		return status;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean scheduleNewJob(SchedulerJobInfo jobInfo) {
		try {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();

			JobDetail jobDetail = JobBuilder
					.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
					.withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
			if (!scheduler.checkExists(jobDetail.getKey())) {
				String uuid = UUID.randomUUID().toString();
				jobDetail = scheduleCreator.createJob(
						(Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), true, context,
						jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo, uuid);

				Trigger trigger;
				if (jobInfo.getCronJob()) {
					trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
							jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getZone());
				} else {
					trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
							jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
				}
				scheduler.scheduleJob(jobDetail, trigger);
				jobInfo.setJobStatus("SCHEDULED");
				jobInfo.setJobId(uuid);
				schedulerRepository.save(jobInfo);
				log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
			} else {
				log.error("scheduleNewJobRequest.jobAlreadyExist");
				return false;
			}
		} catch (ClassNotFoundException e) {
			log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public void updateScheduleJob(SchedulerJobInfo jobInfo) {
		Trigger newTrigger;
		if (jobInfo.getCronJob()) {
			newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
					jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW, jobInfo.getZone());

			// scheduler.getJobDetail(new JobKey(jobInfo.getJobName(),
			// jobInfo.getJobGroup()));

		} else {
			newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime(),
					SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

		}
		try {
			JobKey jobKey = new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup());
			Scheduler scheduler2 = schedulerFactoryBean.getScheduler();
			JobDetail jobDetail = scheduler2.getJobDetail(jobKey);
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("json", jobInfo.getJobDataInput());
			String uuid = jobInfo.getJobId();
			jobDetail = scheduleCreator.createJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
					true, context, jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo, uuid);

			scheduler.addJob(jobDetail, true);

			schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
			jobInfo.setJobStatus("EDITED & SCHEDULED");
			schedulerRepository.save(jobInfo);
			log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getAllJobGroups() {
		List<Group> groupList = Arrays.asList(Group.values());
		List<String> groupListAsStrings = new ArrayList<>();
		groupList.forEach((i) -> groupListAsStrings.add(i.toString()));
		return groupListAsStrings;
	}

	@Override
	public List<SchedulerJobInfoDto> getAllJobListByGroupAndJobName(String jobGroup, String jobName, Pageable pageable) {
		List<SchedulerJobInfo> jobWithGroupNameAndName = schedulerRepository.findByGroupNameAndJobName(jobGroup, jobName, pageable);
		List<SchedulerJobInfoDto> jobWithGroupNameAndNameDtos = new ArrayList<>();
		jobWithGroupNameAndName.forEach((j) -> {
			SchedulerJobInfoDto jobSchedulerDto = dtoUtility.convertToSchedulerJobInfoDto(j);
			jobWithGroupNameAndNameDtos.add(jobSchedulerDto);
		});
		return jobWithGroupNameAndNameDtos;
	}

	@Override
	public List<JobStatusDto> getAllJobListByGroupAndJobNameHistory(String jobGroup, String jobName, Pageable pageable) {
		List<JobStatus> jobHistory = jobStatusRepository.findByGroupAndJobNameHistory(jobGroup, jobName, pageable);
		List<JobStatusDto> jobHistoryDtoList = new ArrayList<>();
		jobHistory.forEach((j) -> {
			JobStatusDto jobStatusDto = dtoUtility.convertToJobStatusDto(j);
			jobHistoryDtoList.add(jobStatusDto);
		});
		return jobHistoryDtoList;
	}
	@Override
	public List<JobStatusDto> getAllJobListByGroupAndJobNameHistory(String jobGroup, String jobName, String jobId,
			Pageable pageable) {
		List<JobStatus> jobHistory = jobStatusRepository.findByGroupAndJobNameHistory(jobGroup, jobName, jobId, pageable);
		List<JobStatusDto> jobHistoryDtoList = new ArrayList<>();
		jobHistory.forEach((j) -> {
			JobStatusDto jobStatusDto = dtoUtility.convertToJobStatusDto(j);
			jobHistoryDtoList.add(jobStatusDto);
		});
		return jobHistoryDtoList;
	}

	@Override
	public SchedulerJobInfo getJobByName(String jobName) {
		SchedulerJobInfo findByJobName = schedulerRepository.findByJobName(jobName);
		return findByJobName;
	}

	@Override
	public SchedulerJobInfo getJobById(String jobId) {
		return schedulerRepository.findById(jobId);
		
	}

}
