package com.challenge.jpa;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"dependents"})
public class Enrollee {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	@NotBlank(message = "Name is mandatory")
	private String name;
	
	private boolean activationStatus;
	
	@Column(nullable = false)
	@NotNull(message = "DateOfBirth is mandatory")
	private LocalDate dateOfBirth;
	
	private String phoneNumber;
	
	@OneToMany(mappedBy = "enrollee", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@Singular
	@JsonIgnore
	private Set<Dependent> dependents;
}
