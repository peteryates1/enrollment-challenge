package com.challenge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.dao.DependentDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

import lombok.extern.slf4j.Slf4j;

/**
 * Bean providing services for the dependent entity.
 */
@Service
@Slf4j
public class DependentService {
	@Autowired
	private DependentDAO dependents;
	
	public Iterable<Dependent> findAll() {
		return dependents.findAll();
	}

	public List<Dependent> findByEnrollee(Enrollee enrollee) {
		return dependents.findByEnrollee(enrollee);
	}

	public <S extends Dependent> S save(S entity) {
		log.info("saving dependent: {}", entity);
		final var saved = dependents.save(entity);
		log.info("saved dependent: {}", saved);
		return saved;
	}

	public Optional<Dependent> findById(Long id) {
		return dependents.findById(id);
	}

	public void deleteById(Long id) {
		log.info("deleting dependent id: {}", id);
		dependents.deleteById(id);
	}
}
