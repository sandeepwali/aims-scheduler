package com.solum.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	
	  @Bean
	  public GroupedOpenApi publicApi() {
	      return GroupedOpenApi.builder()
	              .group("aims-scheduler")
	              .pathsToMatch("/api/**")
	              .build();
	  }
	  
	  @Bean
	  public OpenAPI springShopOpenAPI() {
	      return new OpenAPI()
	              .info(new Info().title("Aims-Scheduler")
	              .description("Aims-Scheduler Portal")
	              .version("v0.0.1")
	              //.license(new License().name("Apache 2.0").url("http://springdoc.org")))
	              //.externalDocs(new ExternalDocumentation()
	              .description("This is the Description of Aims_Scheduler"));
	              //.url("https://springshop.wiki.github.org/docs"));
	  }
}
