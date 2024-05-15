package com.solum.filter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ApiKeyRequestFilter extends GenericFilterBean {
	
	@Value("${solum.filter.apikeyrequestfilter.apiKey}")
	private String apiKey;
	
	private static ObjectMapper oMapper = new ObjectMapper();
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String key = getValidString(req.getHeader("api-key"));

		log.info("Trying key: " + key);

		if(apiKey.equalsIgnoreCase(key)){
			chain.doFilter(request, response);
		}else{
			Map<String, Object> errorInfoMap = new HashMap<String, Object>();
			errorInfoMap.put("errorMessage", "Invalid API-Key");
			errorInfoMap.put("errorCode", HttpServletResponse.SC_UNAUTHORIZED );
			writeMessage(response,  errorInfoMap);   
		}

	}

	private void writeMessage(ServletResponse response,  Object error)
			throws JsonProcessingException, IOException {
		HttpServletResponse resp = (HttpServletResponse) response;
		String errorResponse = oMapper.writeValueAsString(error);
		resp.reset();
		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		out.print(errorResponse);
		out.flush();
	}

	private static String getValidString(Object arg){
		return (arg == null) ? "" : arg.toString().trim();
	}
}