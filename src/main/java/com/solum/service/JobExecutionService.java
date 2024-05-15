package com.solum.service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.solum.entity.FeignResponse;
import com.solum.entity.label.RmaLabels;

@Service
public interface JobExecutionService {

	public FeignResponse postUpdate(UUID requestId, List<RmaLabels> rmaLabels, URI uri, Map<String, String> headersMap); 
}
