package com.challenge.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

/**
 * Data Access Object for dependent entity.
 */
@RepositoryRestResource(exported = false)
public interface DependentDAO extends CrudRepository<Dependent, Long>{
	List<Dependent> findByEnrollee(Enrollee enrollee);
}
