package com.solum.entity.label;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="label")
public class RmaLabels {

	@Id
	private String code;
	private String name;
	private String company;
	private String country;
	private String region;
	private String city;
//	private String labelcode;
	private String state;
	private String type;
	private int display_height;
	private int display_width;
	private Timestamp process_update_time;
	private String status_update_time;
	private int firmware_version;
	private String battery_level;
	private int data_channel_rssi;
	private int wakeup_channel_rssi;
	
	
}
