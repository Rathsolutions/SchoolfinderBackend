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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.postbody.AddNewPersonPostbody;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.util.exception.ResourceNotFoundException;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Sql(scripts = "../../../data-init.sql")
@Transactional
public class PersonControllerTest {

	@Autowired
	private PersonController cut;

	@Test
	void testExistsPersonWithExistingPerson() {
		ResponseEntity<Boolean> alreadyExistingPerson = cut.existsPerson("karl", "test", "karl@test.de", "0815");
		assertTrue(alreadyExistingPerson.getBody());
	}

	@Test
	void testExistsPersonWithNotExistingPerson() {
		ResponseEntity<Boolean> alreadyExistingPerson = cut.existsPerson("karl5", "test", "karl@test.de", "0815");
		assertFalse(alreadyExistingPerson.getBody());
	}

	@Test
	void testGetPersonBlankEmailAndPrename() {
		ResponseEntity<?> responseEntity = cut.getPerson("", "test", "", "");
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals("This person could not be found, please specify more parameters!", responseEntity.getBody());
	}

	@Test
	void testGetPersonBlankEmailAndLastname() {
		ResponseEntity<?> responseEntity = cut.getPerson("test", "", "", "");
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals("This person could not be found, please specify more parameters!", responseEntity.getBody());
	}

	@Test
	void testGetPersonAllBlank() {
		ResponseEntity<?> responseEntity = cut.getPerson("", "", "", "");
		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		assertEquals("This person could not be found, please specify more parameters!", responseEntity.getBody());
	}

	@Test
	@Disabled
	void testGetPersonFilledEmailAndPrename() {
		ResponseEntity<?> responseEntity = cut.getPerson("karl", "", "karl@test.de", "");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0815", ((Person) responseEntity.getBody()).getPhoneNumber());
		assertEquals("karl", ((Person) responseEntity.getBody()).getPrename());
		assertEquals("test", ((Person) responseEntity.getBody()).getLastname());
		assertEquals("karl@test.de", ((Person) responseEntity.getBody()).getEmail());
	}

	@Test
	@Disabled
	void testGetPersonFilledEmailAndLastname() {
		ResponseEntity<?> responseEntity = cut.getPerson("", "test", "karl@test.de", "");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0815", ((Person) responseEntity.getBody()).getPhoneNumber());
		assertEquals("karl", ((Person) responseEntity.getBody()).getPrename());
		assertEquals("test", ((Person) responseEntity.getBody()).getLastname());
		assertEquals("karl@test.de", ((Person) responseEntity.getBody()).getEmail());
	}

	@Test
	void testGetPersonFilledRightValuesAll() {
		ResponseEntity<?> responseEntity = cut.getPerson("karl", "test", "karl@test.de", "0815");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals("0815", ((Person) responseEntity.getBody()).getPhoneNumber());
		assertEquals("karl", ((Person) responseEntity.getBody()).getPrename());
		assertEquals("test", ((Person) responseEntity.getBody()).getLastname());
		assertEquals("karl@test.de", ((Person) responseEntity.getBody()).getEmail());
	}

	@Test
	void testGetPersonFilledEmailAndWrongLastname() {
		assertThrows(ResourceNotFoundException.class, () -> {
			ResponseEntity<?> responseEntity = cut.getPerson("", "test2", "karl@test.de", "");
			assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
			assertNull(responseEntity.getBody());
		});
	}

	@Test
	void testGetPersonFilledEmailAndWrongPrename() {
		assertThrows(ResourceNotFoundException.class, () -> {
			ResponseEntity<?> responseEntity = cut.getPerson("karl2", "", "karl@test.de", "");
			assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
			assertNull(responseEntity.getBody());
		});
//		assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
		
	}

	@Test
	void testGetEmailRecommendationsRightPrenameRightLastnameEmptyEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl", "test", "", "10");
		assertEquals(1, emailRecommendations.getBody().size());
		assertEquals(emailRecommendations.getBody().get(0).getEmail(), "karl@test.de");
	}

	@Test
	void testGetEmailRecommendationsRightPrenameWrongLastnameEmptyEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl", "test2", "", "10");
		assertEquals(0, emailRecommendations.getBody().size());
	}

	@Test
	void testGetEmailRecommendationsWrongPrenameRightLastnameEmptyEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl3", "test", "", "10");
		assertEquals(0, emailRecommendations.getBody().size());
	}

	@Test
	void testGetEmailRecommendationsWrongPrenameWrongLastnameCorrectEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl3", "test2",
				"karl@test.de", "10");
		assertEquals(0, emailRecommendations.getBody().size());
	}

	@Test
	void testGetEmailRecommendationsWrongPrenameWrongLastnameWrongEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl3", "test2",
				"karl3@test.de", "10");
		assertEquals(0, emailRecommendations.getBody().size());
	}

	@Test
	void testGetEmailRecommendationsRightPrenameRightLastnameWrongEmail() {
		ResponseEntity<List<Person>> emailRecommendations = cut.getEmailRecommendations("karl", "test", "karl3@test.de",
				"10");
		assertEquals(0, emailRecommendations.getBody().size());
	}

	@Test
	void testAddNewPersonPersonWithOnlyPrenameAlreadyExisting() {
		AddNewPersonPostbody personPostbody = new AddNewPersonPostbody("karl", "test2", "karl@test2.de", "0817");
		ResponseEntity<Person> newPerson = cut.addNewPerson(personPostbody);
		assertEquals(personPostbody.getPrename(), newPerson.getBody().getPrename());
		assertEquals(personPostbody.getLastname(), newPerson.getBody().getLastname());
		assertEquals(personPostbody.getEmail(), newPerson.getBody().getEmail());
		assertEquals(personPostbody.getPhoneNumber(), newPerson.getBody().getPhoneNumber());
	}

	@Test
	void testAddNewPersonPersonWithOnlyLastnameAlreadyExisting() {
		AddNewPersonPostbody personPostbody = new AddNewPersonPostbody("karl3", "test", "karl@test2.de", "0817");
		ResponseEntity<Person> newPerson = cut.addNewPerson(personPostbody);
		assertEquals(personPostbody.getPrename(), newPerson.getBody().getPrename());
		assertEquals(personPostbody.getLastname(), newPerson.getBody().getLastname());
		assertEquals(personPostbody.getEmail(), newPerson.getBody().getEmail());
		assertEquals(personPostbody.getPhoneNumber(), newPerson.getBody().getPhoneNumber());
	}

	@Test
	void testAddNewPersonPersonWithOnlyEmailAlreadyExisting() {
		AddNewPersonPostbody personPostbody = new AddNewPersonPostbody("karl3", "test2", "karl@test.de", "0817");
		ResponseEntity<Person> newPerson = cut.addNewPerson(personPostbody);
		assertEquals(HttpStatus.OK, newPerson.getStatusCode());
		assertNotNull(newPerson.getBody());
	}

	@Test
	void testAddNewPersonPersonWithOnlyPhoneNumberAlreadyExisting() {
		AddNewPersonPostbody personPostbody = new AddNewPersonPostbody("karl3", "test2", "karl2@test.de", "0815");
		ResponseEntity<Person> newPerson = cut.addNewPerson(personPostbody);
		assertEquals(HttpStatus.OK, newPerson.getStatusCode());
		assertNotNull(newPerson.getBody());
	}

}
