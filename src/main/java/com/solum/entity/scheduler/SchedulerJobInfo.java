package com.solum.entity.scheduler;

import java.io.Serializable;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "SchedulerJobInfo", uniqueConstraints=@UniqueConstraint(columnNames={"jobname", "jobName"}))
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchedulerJobInfo implements Serializable{

   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    private String jobId;
	//@Column(name = "jobname",unique=true)
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String description;    
    private String interfaceName;
    private Long repeatTime;
    private Boolean cronJob;
    @Column(columnDefinition="TEXT")
    private String jobDataInput;
    private TimeZone zone;
    
    
    
}
