/*-
 * #%L
 * SchuglemapsBackend
 * %%
 * Copyright (C) 2020 Rathsolutions. <info@rathsolutions.de>
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package de.rathsolutions.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.controller.postbody.AddNewPersonPostbody;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.jpa.entity.PersonSchoolMapping;
import de.rathsolutions.jpa.repo.PersonRepo;
import de.rathsolutions.jpa.repo.PersonSchoolMappingRepo;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {
    private static final String THIS_PERSON_COULD_NOT_BE_FOUND_PLEASE_SPECIFY_MORE_PARAMETERS = "This person could not be found, please specify more parameters!";

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private PersonSchoolMappingRepo personSchoolMappingRepo;

    @Operation(summary = "checks if a person already exists")
    @GetMapping("/search/existsPerson")
    public ResponseEntity<Boolean> existsPerson(String prename, String lastname, String email, String phonenumber) {
	return ResponseEntity
		.ok(personRepo.existsByPrenameAndLastnameAndEmailAndPhoneNumber(prename, lastname, email, phonenumber));
    }

    @Operation(summary = "queries for all persons according to a school id")
    @GetMapping("/search/getPersonsForSchool")
    public ResponseEntity<List<PersonSchoolMapping>> getPersonsForSchool(long id) {
	List<PersonSchoolMapping> personBySchoolId = personSchoolMappingRepo.findOneBySchoolId(id);
	if (personBySchoolId.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(personBySchoolId);
    }

    @Operation(summary = "queries for a database-saved person resource")
    @GetMapping("/search/getPerson")
    public ResponseEntity<?> getPerson(String prename, String lastname, String email) {
	if (prename.isBlank() && lastname.isBlank() && email.isBlank()
		|| email.isBlank() && (prename.isBlank() || lastname.isBlank())) {
	    return ResponseEntity.badRequest().body(THIS_PERSON_COULD_NOT_BE_FOUND_PLEASE_SPECIFY_MORE_PARAMETERS);
	}
	Optional<Person> person = null;
	if (!email.isBlank() && personRepo.countByEmail(email) == 1) {
	    person = personRepo.findByEmail(email);
	} else if (email.isBlank() && personRepo.countByPrenameAndLastname(prename, lastname) == 1) {
	    person = personRepo.findByPrenameAndLastname(prename, lastname);
	} else if (personRepo.countByPrenameAndLastnameAndEmail(prename, lastname, email) == 1) {
	    person = personRepo.findByPrenameAndLastnameAndEmail(prename, lastname, email);
	} else {
	    throw new ResourceNotFoundException(prename + lastname + email, "person");
	}
	if (person.isEmpty()) {
	    throw new ResourceNotFoundException(person, "person");
	}
	if ((!lastname.isBlank() && !person.get().getLastname().equalsIgnoreCase(lastname))
		|| (!prename.isBlank() && !person.get().getPrename().equalsIgnoreCase(prename))) {
	    return ResponseEntity.badRequest().build();
	}
	if (person != null && person.isPresent()) {
	    return ResponseEntity.ok(person.get());
	}
	return ResponseEntity.notFound().build();
    }

    @Operation(summary = "queries for email address recommendations by specific person criterias")
    @GetMapping("/search/getEmailRecommendations")
    public ResponseEntity<List<Person>> getEmailRecommendations(String prename, String lastname, String email,
	    String amount) {
	PageRequest page = PageRequest.of(0, Integer.valueOf(amount));
	return ResponseEntity.ok(personRepo.findByPrenameAndLastnameAndEmailContaining(prename, lastname, email, page));
    }

    @Operation(summary = "creates a new person resource in the database")
    @PutMapping("/create/addNewPerson")
    public ResponseEntity<Person> addNewPerson(@RequestBody AddNewPersonPostbody addNewPersonPostbody) {
	if (personRepo.findByPrenameAndLastnameAndEmailAndPhoneNumber(addNewPersonPostbody.getPrename(),
		addNewPersonPostbody.getLastname(), addNewPersonPostbody.getEmail(),
		addNewPersonPostbody.getPhoneNumber()).isPresent()) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	Optional<Person> personByEmail = personRepo.findByEmail(addNewPersonPostbody.getEmail());
	if (personByEmail.isPresent()
		&& (personByEmail.get().getPhoneNumber().equals(addNewPersonPostbody.getPhoneNumber())
			|| personByEmail.get().getEmail().equals(addNewPersonPostbody.getEmail()))
		&& (!personByEmail.get().getPrename().equalsIgnoreCase(addNewPersonPostbody.getPrename())
			|| !personByEmail.get().getLastname().equalsIgnoreCase(addNewPersonPostbody.getLastname()))) {
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
	Person person = new Person(addNewPersonPostbody.getPrename(), addNewPersonPostbody.getLastname(),
		addNewPersonPostbody.getEmail(), addNewPersonPostbody.getPhoneNumber());
	return ResponseEntity.ok(personRepo.save(person));
    }
}
