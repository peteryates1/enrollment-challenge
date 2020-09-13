package com.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=ChallengeApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class ModelTest {
	
	@Autowired
    private TestRestTemplate template;
	
	@LocalServerPort
    private int port;
	
    private String getRootUrl() {
        return "http://localhost:" + port;
    }
    
    private String getEnrolleesUrl() {
        return getRootUrl()+"/api/v1/enrollees";
    }
    
    private String getDependentsUrl() {
        return getRootUrl()+"/api/v1/dependents";
    }
    
    private boolean debug = log.isDebugEnabled();
    
	@Test
	public void getEnrollees() {
		String url = getEnrolleesUrl();
		if(debug) log.debug(url);
		ResponseEntity<Enrollee[]> responseEntity = template.getForEntity(url, Enrollee[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		if(debug) log.debug("StatusCode: "+responseEntity.getStatusCode());
		if(debug) log.debug("responseEntity.getBody(): "+responseEntity.getBody()[0]);
	}
    
	@Test
	public void createGetDeleteEnrollee() {
		String name = "enrollee 1";
		boolean activationStatus = true;
		LocalDate dateOfBirth = LocalDate.now().minusYears(44);
		Enrollee savedEnrollee = createEnrollee(name, activationStatus, dateOfBirth);
		assertNotNull(savedEnrollee.getId());
		
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId();
		if(debug) log.debug(url);
		ResponseEntity<Enrollee> responseEntity = template.getForEntity(url, Enrollee.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		if(debug) log.debug("StatusCode: "+responseEntity.getStatusCode());
		savedEnrollee = responseEntity.getBody();
		assertNotNull(savedEnrollee.getId());
		assertEquals(name, savedEnrollee.getName());
		assertEquals(activationStatus, savedEnrollee.isActivationStatus());
		assertEquals(dateOfBirth, savedEnrollee.getDateOfBirth());
		
		template.delete(url);
		
		responseEntity = template.getForEntity(url, Enrollee.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	public void createEnrollee() {
		String name = "enrollee 1", phoneNumber = "555-7863";
		boolean activationStatus = true;
		LocalDate dateOfBirth = LocalDate.now().minusYears(44);
		Enrollee savedEnrollee = createEnrollee(name, activationStatus, dateOfBirth, phoneNumber);
		assertNotNull(savedEnrollee.getId());
		assertEquals(name, savedEnrollee.getName());
		assertEquals(activationStatus, savedEnrollee.isActivationStatus());
		assertEquals(dateOfBirth, savedEnrollee.getDateOfBirth());
		assertEquals(phoneNumber, savedEnrollee.getPhoneNumber());
	}
	
	@Test
	public void createEnrolleeNoName() {
		String url = getEnrolleesUrl();
		boolean activationStatus = true;
		LocalDate dateOfBirth = LocalDate.now().minusYears(44);
		Enrollee newEnrollee = Enrollee.builder()
				.activationStatus(activationStatus)
				.dateOfBirth(dateOfBirth)
				.build();
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newEnrollee, Map.class);
		if(debug) log.debug("responseEntity: "+responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void createEnrolleeNoDateOfBirth() {
		String url = getEnrolleesUrl(),
			name = "enrollee 1";
		boolean activationStatus = true;
		Enrollee newEnrollee = Enrollee.builder()
				.name(name)
				.activationStatus(activationStatus)
				.build();
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newEnrollee, Map.class);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void createEnrolleeAndDependentAndDeleteDependent() {
		String enrolleeName = "enrollee with dependent";
		boolean activationStatus = true;
		LocalDate enrolleeDateOfBirth = LocalDate.now().minusYears(44);
		
		Enrollee savedEnrollee = createEnrollee(enrolleeName, activationStatus, enrolleeDateOfBirth);
		assertNotNull(savedEnrollee.getId());
		
		String dependentName = "dependent";
		LocalDate dependentDateOfBirth = LocalDate.now().minusYears(14);
		
		Dependent savedDependent = createDependent(savedEnrollee.getId(), dependentName, dependentDateOfBirth);
		assertNotNull(savedDependent.getId());
		assertEquals(dependentName, savedDependent.getName());
		assertEquals(dependentDateOfBirth, savedDependent.getDateOfBirth());
		assertEquals(dependentDateOfBirth, savedDependent.getDateOfBirth());
		
		template.delete(getDependentsUrl()+"/"+savedDependent.getId());
	}
	
	@Test
	public void createDependentAndNoName() {
		Enrollee savedEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(44));
		assertNotNull(savedEnrollee.getId());
		Dependent newDependent = Dependent.builder()
				.dateOfBirth(LocalDate.now().minusYears(14))
				.build();
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId()+"/dependent";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newDependent, Map.class);
		if(debug) log.debug("responseEntity: "+responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void createDependentAndNoDateOfBirth() {
		Enrollee savedEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(44));
		assertNotNull(savedEnrollee.getId());
		Dependent newDependent = Dependent.builder()
				.name("dependent")
				.build();
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId()+"/dependent";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newDependent, Map.class);
		if(debug) log.debug("responseEntity: "+responseEntity);
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	private Enrollee createEnrollee(String name, boolean activationStatus, LocalDate dateOfBirth) {
		return createEnrollee(name, activationStatus, dateOfBirth, null);
	}
	
	private Enrollee createEnrollee(String name, boolean activationStatus, LocalDate dateOfBirth, String phoneNumber) {
		String url = getEnrolleesUrl();
		Enrollee newEnrollee = Enrollee.builder()
				.name(name)
				.activationStatus(activationStatus)
				.dateOfBirth(dateOfBirth)
				.phoneNumber(phoneNumber)
				.build();
		ResponseEntity<Enrollee> responseEntity = template.postForEntity(url, newEnrollee, Enrollee.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		return responseEntity.getBody();
	}
	
	private Dependent createDependent(Long enrolleeId, String name, LocalDate dateOfBirth) {
		String url = getEnrolleesUrl()+"/"+enrolleeId+"/dependent";
		Dependent newDependent = Dependent.builder()
				.name(name)
				.dateOfBirth(dateOfBirth)
				.build();
		ResponseEntity<Dependent> responseEntity = template.postForEntity(url, newDependent, Dependent.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		return responseEntity.getBody();
	}
}
