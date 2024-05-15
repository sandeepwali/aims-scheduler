package com.solum.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDataInput {
	private String job;
	private JobParameters jobParameters;
	private List<JobOutputList> jobOutputList;
}
