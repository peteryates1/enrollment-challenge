package com.challenge.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.dao.EnrolleeDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

@Service
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
		return enrollees.save(entity);
	}

	public <S extends Dependent> S save(Long enrolleeId, S dependent) {
		dependent.setEnrollee(enrollees.findById(enrolleeId).get());
		return dependentService.save(dependent);
	}

	public void deleteById(Long enrolleeId) {
		enrollees.deleteById(enrolleeId);
	}
}
