package com.solum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author baskarmohanasundaram
 *
 */
@Configuration
@Getter
@Setter
public class GraphClientConfig {

	@Value("${scheduler.graph.clientid:}")
	private String clientId;
	@Value("${scheduler.graph.secret:}")
	private String clientSecret;
	@Value("${scheduler.graph.tenantid:}")
	private String tenantId;
	@Value("${scheduler.graph.scopes:.default}")
	private String scopes;
	@Value("${scheduler.graph.fromemailadddress:redmine@solumesl.com}")
	private String fromEmailAddress;
	@Value("${scheduler.graph.proxyhost:}")
	private String proxyHost;
	@Value("${scheduler.graph.proxyport:80}")
	private Integer proxyPort;

}
