package com.solum.entity;

import java.util.TimeZone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerJobInfoDto {

    private String jobId;
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String jobClass;
    private String cronExpression;
    private String description;    
    private String interfaceName;
    private Long repeatTime;
    private Boolean cronJob;
    private TimeZone zone;
    //private JobDataInput jobDataInput;
    
	
}
