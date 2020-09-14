package com.challenge.jpa;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


/**
 * @author peter
 * A dependent of an enrollee.
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"enrollee"})
public class Dependent {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false)
	@NotBlank(message = "Name is mandatory")
	private String name;
	
	@Column(nullable = false)
	@NotNull(message = "DateOfBirth is mandatory")
	private LocalDate dateOfBirth;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonIgnore
	private Enrollee enrollee;
}
