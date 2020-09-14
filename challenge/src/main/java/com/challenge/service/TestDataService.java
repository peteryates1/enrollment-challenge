package com.challenge.service;

import java.time.LocalDate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.dao.DependentDAO;
import com.challenge.dao.EnrolleeDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

import lombok.extern.slf4j.Slf4j;

/**
 * Service to provide test data
 */
@Service
@Slf4j
public class TestDataService {
	
	@Autowired
	private DependentDAO dependents;
	
	@Autowired
	private EnrolleeDAO enrollees;
	
	@Transactional
	public void load() {
		log.info("Loading test data.");
		dependents.deleteAll();
		enrollees.deleteAll();
		
		Enrollee peter = enrollees.save(Enrollee.builder()
				.activationStatus(true)
				.dateOfBirth(LocalDate.now().minusYears(37))
				.name("Peter")
				.phoneNumber("555-555-5555")
				.build());
		dependents.save(Dependent.builder()
				.enrollee(peter)
				.dateOfBirth(LocalDate.now().minusYears(6))
				.name("Paul")
				.build());
		dependents.save(Dependent.builder()
				.enrollee(peter)
				.dateOfBirth(LocalDate.now().minusYears(4))
				.name("Mary")
				.build());
		
		Enrollee john = enrollees.save(Enrollee.builder()
				.activationStatus(false)
				.dateOfBirth(LocalDate.now().minusYears(47))
				.name("John")
				.build());
		dependents.save(Dependent.builder()
				.enrollee(john)
				.dateOfBirth(LocalDate.now().minusYears(4))
				.name("Ringo")
				.build());
		dependents.save(Dependent.builder()
				.enrollee(john)
				.dateOfBirth(LocalDate.now().minusYears(4))
				.name("George")
				.build());
		dependents.save(Dependent.builder()
				.enrollee(john)
				.dateOfBirth(LocalDate.now().minusYears(4))
				.name("Paul")
				.build());
		
		enrollees.save(Enrollee.builder()
				.activationStatus(true)
				.dateOfBirth(LocalDate.now().minusYears(47))
				.name("Han Solo")
				.build());
		
		log.info("Test data loaded.");
	}
}
