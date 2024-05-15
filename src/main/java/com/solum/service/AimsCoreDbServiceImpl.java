package com.solum.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.solum.entity.FeignResponse;
import com.solum.feign.AimsCoreDbServiceProxy;

import feign.FeignException.FeignClientException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AimsCoreDbServiceImpl implements AimsCoreDbService{

	
	@Autowired
	private AimsCoreDbServiceProxy aimsCoreDbServiceProxy;
	
	@Value("${aims.api.key}")
	private String api_key;
	
	@Override
	public FeignResponse deleteLable(UUID requestId, String labelCode) {
		log.debug("Request to delete label haivng Id: {}", requestId);
		try {
			ResponseEntity<Object> response = aimsCoreDbServiceProxy.deleteLabelFromDb(api_key, labelCode);
			if(!ObjectUtils.isEmpty(response) && response.getStatusCode().is2xxSuccessful()) {
				log.info("Calls to AIMS Core DB to delete label with RequestId: {} is successful",requestId);
				FeignResponse feignResponse = FeignResponse.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.TRUE)
						.errorMessage(null)
						.build();
				return feignResponse;
			}else {
				log.error("Calls to AIMS Core DB to delete label with RequestId: {} and labelCode: {} is failed with response {}",requestId, labelCode, response);
				//@TODO handle error scenerios properly after testing
				return FeignResponse.builder()
						.statusCode(response.getStatusCode().value())
						.success(Boolean.FALSE)
						.errorMessage(ObjectUtils.isEmpty(response.getBody())? "" : response.getBody().toString())
						.build();
			}
		} catch(FeignClientException e) {
			log.error("Calls to AIMS Core DB to delete Label with RequestId: {} and labelCode: {} is failed with response {}",requestId, labelCode, e.getMessage());
			return FeignResponse.builder()
					.statusCode(e.status())
					.success(Boolean.FALSE)
					.errorMessage((e.status() == 404)? HttpStatus.NOT_FOUND.name() : e.getMessage())
					.build();
		}

	}

}
