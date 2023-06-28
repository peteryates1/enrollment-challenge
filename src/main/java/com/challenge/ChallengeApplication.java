package com.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.challenge.service.TestDataService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Spring boot application entry point.
 */
@SpringBootApplication
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
public class ChallengeApplication {
	
	/**
	 * Command line option used to create test data
	 */
	public static final String CREATE_TEST_DATA = "CreateTestData"; 
	
	@Autowired
	private TestDataService testDataService;
	
	public static void main(final String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}
	
	@Bean
	public ApplicationRunner applicationRunner() {
		return args -> {
			if(args.containsOption(CREATE_TEST_DATA)) {
				testDataService.load();
			}
		};
	}
}
