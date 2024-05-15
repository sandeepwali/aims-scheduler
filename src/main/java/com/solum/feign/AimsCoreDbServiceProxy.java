package com.solum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.solum.config.feign.FeignConfiguration;

@FeignClient(name = "data", url = "${aims.baseurl}", configuration = FeignConfiguration.class)
public interface AimsCoreDbServiceProxy {

	@RequestMapping(method = RequestMethod.DELETE, value = "${aims.api}/{labelcode}")
	public ResponseEntity<Object> deleteLabelFromDb(@RequestHeader("api-key") String apiKey, @PathVariable String labelcode);

}

