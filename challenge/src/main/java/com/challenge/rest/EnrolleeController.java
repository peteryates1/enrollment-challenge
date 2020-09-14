package com.challenge.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;
import com.challenge.service.EnrolleeService;

/**
 * Rest controller for enrollee entity.
 */
@RestController
@RequestMapping("/api/v1")
public class EnrolleeController {
	
	@Autowired private EnrolleeService enrolleeService;

	@GetMapping(
		path = "/enrollees",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Iterable<Enrollee> getEnrollees() {
		return enrolleeService.findAll();
	}
	
	@GetMapping(
		path = "/enrollees/{enrolleeId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Enrollee> getEnrollee(@PathVariable("enrolleeId") Long enrolleeId) {
		try {
			return ResponseEntity.ok(enrolleeService.findById(enrolleeId).get());
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping(
		path = "/enrollees/{enrolleeId}/dependents",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Iterable<Dependent>> getDependents(@PathVariable("enrolleeId") Long enrolleeId) {
		try {
			return ResponseEntity.ok(enrolleeService.findDependentsByEnrolleeId(enrolleeId));
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/enrollees")
    public Enrollee saveEnrollee(@Validated @RequestBody Enrollee enrollee) {
        return enrolleeService.save(enrollee);
    }
	
	@PostMapping("/enrollees/{enrolleeId}/dependent")
    public Dependent saveDependent(@PathVariable("enrolleeId") Long enrolleeId, @Validated @RequestBody Dependent dependent) {
        return enrolleeService.save(enrolleeId, dependent);
    }
	
	@DeleteMapping(
		path = "/enrollees/{enrolleeId}",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	public void deleteEnrollee(@PathVariable("enrolleeId") Long enrolleeId) {
		enrolleeService.deleteById(enrolleeId);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
	    Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach((error) -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
	    return errors;
	}
}
