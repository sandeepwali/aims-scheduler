package com.solum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobParameters {

	private String areaKey;
	private String areaValue;
	private int rmaThreshold;
}
