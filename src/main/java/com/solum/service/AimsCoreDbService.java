package com.solum.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.solum.entity.FeignResponse;

@Service
public interface AimsCoreDbService {

	FeignResponse deleteLable(UUID requestId, String labelCode); 
}
