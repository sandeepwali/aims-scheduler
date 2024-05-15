package com.solum.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.solum.repository.label.RmaRepository;
import com.solum.repository.scheduler.SchedulerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisallowConcurrentExecution
public class SampleStoreJob extends QuartzJobBean {
	
	
	@Autowired
	private SchedulerRepository schedulerRepository;
	
	@Autowired
	private RmaRepository rmaRepository;
	
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("SampleStoreJob Start................");
         
        log.info("SampleStoreJob End................");
    }
}
