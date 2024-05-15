package com.solum.feign;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.solum.config.feign.FeignConfiguration;
import com.solum.entity.label.RmaLabels;

@FeignClient(name = "JobExecution", url = "${aims.baseurl}", configuration = FeignConfiguration.class)
public interface JobExecutionApiProxy {
	// value = "${aims.api}/{labelcode}  // ,
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> postUpdate(URI baseUrl, @RequestBody List<RmaLabels> rmaLabels, @RequestHeader Map<String, String> headersMap);

}

