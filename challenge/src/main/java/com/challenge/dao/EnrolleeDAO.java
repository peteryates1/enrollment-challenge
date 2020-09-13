package com.challenge.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.challenge.jpa.Enrollee;

@RepositoryRestResource(exported = false)
public interface EnrolleeDAO extends CrudRepository<Enrollee, Long>{}
