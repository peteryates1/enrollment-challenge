package com.challenge.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.challenge.jpa.Enrollee;

/**
 * Data Access Object for enrollee entity.
 */
@RepositoryRestResource(exported = false)
public interface EnrolleeDAO extends CrudRepository<Enrollee, Long>{}
