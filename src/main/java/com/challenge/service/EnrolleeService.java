package com.challenge.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.dao.EnrolleeDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

import lombok.extern.slf4j.Slf4j;

/**
 * Bean providing services for the enrollee entity.
 */
@Service
@Slf4j
public class EnrolleeService {
	
	@Autowired
	private EnrolleeDAO enrollees;
	
	@Autowired
	private DependentService dependentService;
	
	public Optional<Enrollee> findById(Long enrolleeId) {
		return enrollees.findById(enrolleeId);
	}

	public Iterable<Enrollee> findAll() {
		return enrollees.findAll();
	}

	public Iterable<Dependent> findDependentsByEnrolleeId(Long enrolleeId) {
		return dependentService.findByEnrollee(enrollees.findById(enrolleeId).get());
	}
	
	public <S extends Enrollee> S save(S entity) {
		log.info("saving enrollee: {}", entity);
		final var saved = enrollees.save(entity);
		log.info("saved enrollee: {}", saved);
		return saved;
	}

	public <S extends Dependent> S save(Long enrolleeId, S dependent) {
		log.info("saving dependent for enrolleeId: {}: dependent", enrolleeId, dependent);
		dependent.setEnrollee(enrollees.findById(enrolleeId).get());
		final var saved = dependentService.save(dependent); 
		log.info("saved dependent: {}", saved);
		return saved;
	}

	public void deleteById(Long enrolleeId) {
		log.info("deleting enrolleeId: {}", enrolleeId);
		enrollees.deleteById(enrolleeId);
	}
}
