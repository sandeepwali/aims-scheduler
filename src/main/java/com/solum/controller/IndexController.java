package com.solum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.solum.entity.scheduler.SchedulerJobInfo;
import com.solum.service.SchedulerJobService;

@Controller
public class IndexController {

	@Autowired
	private SchedulerJobService scheduleJobService;
	
	@GetMapping("/")
	public String index(Model model){
		List<SchedulerJobInfo> jobList = scheduleJobService.getAllJobList();
		model.addAttribute("jobs", jobList);
		System.out.println(jobList);
		return "index";
	}
	
}
