package com.solum.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.solum.filter.ApiKeyRequestFilter;

import jakarta.servlet.Filter;

@Configuration
public class FilterConfig {
	@Value("${solum.filter.apikeyrequestfilter.urlPath}")
	private String urlPath;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public FilterRegistrationBean someFilterRegistration() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
	    registration.setFilter(apiKeyRequestFilter());
	    registration.addUrlPatterns(urlPath);
	    registration.setName("apiKeyRequestFilter");
	    registration.setOrder(1);
	    return registration;
	}
	
	@Bean(name = "apiKeyRequestFilter")
	public Filter apiKeyRequestFilter() {
	    return new ApiKeyRequestFilter();
	}
}
