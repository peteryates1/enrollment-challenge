package com.challenge.rest;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.jpa.Dependent;
import com.challenge.service.DependentService;

@RestController
@RequestMapping("/api/v1")
public class DependentController {
	
	@Autowired
	private DependentService dependentService;
	
	@GetMapping(
		path = "/dependents/{dependentId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Dependent> getDependent(@PathVariable("dependentId") Long dependentId) {
		try {
			return ResponseEntity.ok(dependentService.findById(dependentId).get());
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping(
		path = "/dependents/{dependentId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public void deleteDependent(@PathVariable("dependentId") Long dependentId) {
		dependentService.deleteById(dependentId);
	}
		
}
