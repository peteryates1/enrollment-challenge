package com.challenge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.challenge.service.TestDataService;

/**
 * Spring boot application entry point. 
 */
@SpringBootApplication
public class ChallengeApplication implements ApplicationRunner {
	
	public static final String CREATE_TEST_DATA = "CreateTestData"; 
	
	public static void main(String[] args) {
		SpringApplication.run(ChallengeApplication.class, args);
	}
	
	@Autowired
	private TestDataService testDataService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if(args.containsOption(CREATE_TEST_DATA)) {
			testDataService.load();
		}
	}
}
