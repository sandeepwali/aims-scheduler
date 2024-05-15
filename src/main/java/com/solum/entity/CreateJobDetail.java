package com.solum.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateJobDetail {

	private String cronExpression;
	private String description;
	private String zone;
	private JobDataInput jobDataInput;

}
