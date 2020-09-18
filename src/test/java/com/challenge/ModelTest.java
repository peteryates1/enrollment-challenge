package com.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
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

import com.challenge.dao.DependentDAO;
import com.challenge.dao.EnrolleeDAO;
import com.challenge.jpa.Dependent;
import com.challenge.jpa.Enrollee;

import lombok.extern.slf4j.Slf4j;

/**
 * Test the model implementation through REST endpoints.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=ChallengeApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Slf4j
public class ModelTest {
	
	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private EnrolleeDAO enrollees;
	
	@Autowired
	private DependentDAO dependents;
	
	@LocalServerPort
	private int port;
	
	private final boolean debug = log.isDebugEnabled();
	
	private String getRootUrl() {
		return "http://localhost:" + port;
	}
	
	private String getEnrolleesUrl() {
		return getRootUrl()+"/api/v1/enrollees";
	}
	
	private String getDependentsUrl() {
		return getRootUrl()+"/api/v1/dependents";
	}
	
	@BeforeEach
	@Transactional
	public void beforeEach() {
		dependents.deleteAll();
		enrollees.deleteAll();
	}
	
	@Test
	public void testGetEnrollees() {
		createEnrolleeAndDependents();
		ResponseEntity<Enrollee[]> responseEntity = template.getForEntity(getEnrolleesUrl(), Enrollee[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody()[0]);
	}
	
	@Test
	public void testCreateGetDeleteEnrollee() {
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
	public void testGetEnrolleeNotFound() {
		String url = getEnrolleesUrl()+"/1";
		ResponseEntity<Enrollee> responseEntity = template.getForEntity(url, Enrollee.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	public void testCreateEnrollee() {
		String name = "enrollee 1",
				phoneNumber = "555-7863";
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
	public void testCreateEnrolleeNoName() {
		String url = getEnrolleesUrl();
		boolean activationStatus = true;
		LocalDate dateOfBirth = LocalDate.now().minusYears(44);
		Enrollee newEnrollee = Enrollee.builder()
				.activationStatus(activationStatus)
				.dateOfBirth(dateOfBirth)
				.build();
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newEnrollee, Map.class);
		if(debug) {
			log.debug("responseEntity: "+responseEntity); // NOPMD by peter on 9/14/20, 3:19 PM
		}
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void testCreateEnrolleeNoDateOfBirth() {
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
	public void testCreateEnrolleeAndDependentAndDeleteDependent() {
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
	public void testCreateDependentAndNoName() {
		Enrollee savedEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(44));
		assertNotNull(savedEnrollee.getId());
		Dependent newDependent = Dependent.builder()
				.dateOfBirth(LocalDate.now().minusYears(14))
				.build();
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId()+"/dependent";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newDependent, Map.class);
		if(debug) {
			log.debug("responseEntity: "+responseEntity);
		}
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void testCreateDependentAndNoDateOfBirth() {
		Enrollee savedEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(44));
		assertNotNull(savedEnrollee.getId());
		Dependent newDependent = Dependent.builder()
				.name("dependent")
				.build();
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId()+"/dependent";
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = template.postForEntity(url, newDependent, Map.class);
		if(debug) {
			log.debug("responseEntity: "+responseEntity);
		}
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}
	
	@Test
	public void testGetDependentsForEnrollee() {
		Enrollee savedEnrollee = createEnrolleeAndDependents();
		String url = getEnrolleesUrl()+"/"+savedEnrollee.getId()+"/dependents";
		ResponseEntity<Dependent[]> responseEntity = template.getForEntity(url, Dependent[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(2, responseEntity.getBody().length);
	}
	
	@Test
	public void testGetDependentsForEnrolleeNotFound() {
		String url = getEnrolleesUrl()+"/1/dependents";
		ResponseEntity<Dependent[]> responseEntity = template.getForEntity(url, Dependent[].class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	@Test
	public void testGetDependents() {
		createEnrolleeAndDependents();
		String url = getDependentsUrl();
		ResponseEntity<Dependent[]> responseEntity = template.getForEntity(url, Dependent[].class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(2, responseEntity.getBody().length);
	}
	
	@Test
	public void testGetDependent() {
		Enrollee savedEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(44));
		Dependent dependent = createDependent(savedEnrollee.getId(), "dependent", LocalDate.now().minusYears(14));
		String url = getDependentsUrl()+"/"+dependent.getId();
		ResponseEntity<Dependent> responseEntity = template.getForEntity(url, Dependent.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
	}
	
	@Test
	public void testGetDependentNotFound() {
		String url = getDependentsUrl()+"/1";
		ResponseEntity<Dependent> responseEntity = template.getForEntity(url, Dependent.class);
		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}
	
	private Enrollee createEnrollee(final String name, final boolean activationStatus, final LocalDate dateOfBirth) {
		return createEnrollee(name, activationStatus, dateOfBirth, null);
	}
	
	private Enrollee createEnrollee(final String name, final boolean activationStatus, final LocalDate dateOfBirth, final String phoneNumber) {
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
	
	private Dependent createDependent(final Long enrolleeId, final String name, final LocalDate dateOfBirth) {
		String url = getEnrolleesUrl()+"/"+enrolleeId+"/dependent";
		Dependent newDependent = Dependent.builder()
				.name(name)
				.dateOfBirth(dateOfBirth)
				.build();
		ResponseEntity<Dependent> responseEntity = template.postForEntity(url, newDependent, Dependent.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		return responseEntity.getBody();
	}
	
	private Enrollee createEnrolleeAndDependents() {
		Enrollee newEnrollee = createEnrollee("enrollee", true, LocalDate.now().minusYears(30));
		createDependent(newEnrollee.getId(), "dependent 1", LocalDate.now().minusYears(10));
		createDependent(newEnrollee.getId(), "dependent 2", LocalDate.now().minusYears(11));
		return newEnrollee;
	}
}
