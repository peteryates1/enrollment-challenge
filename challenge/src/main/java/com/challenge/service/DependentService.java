package com.challenge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.dao.DependentDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

@Service
public class DependentService {
	@Autowired
	private DependentDAO dependents;
	
	public List<Dependent> findByEnrollee(Enrollee enrollee) {
		return dependents.findByEnrollee(enrollee);
	}

	public <S extends Dependent> S save(S entity) {
		return dependents.save(entity);
	}

	public Optional<Dependent> findById(Long id) {
		return dependents.findById(id);
	}

	public void deleteById(Long id) {
		dependents.deleteById(id);
	}
}
