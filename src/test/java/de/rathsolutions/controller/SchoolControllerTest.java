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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;
import org.xml.sax.SAXException;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.postbody.PersonFunctionalityDTO;
import de.rathsolutions.controller.postbody.ProjectDTO;
import de.rathsolutions.controller.postbody.SchoolDTO;
import de.rathsolutions.controller.postbody.SchoolTypeDTO;
import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.entity.Functionality;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.entity.SchoolTypeValue;
import de.rathsolutions.jpa.repo.CriteriaRepo;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.util.exception.BadArgumentsException;
import de.rathsolutions.util.exception.ResourceAlreadyExistingException;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.FinderEntitySearchConstraint;
import de.rathsolutions.util.finder.pojo.SchoolSearchEntity;
import de.rathsolutions.util.finder.specific.osm.OsmPOISchoolParser;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Sql(scripts = "../../../data-init.sql")
public class SchoolControllerTest {

	private static final SchoolTypeDTO SCHOOL_TYPE = new SchoolTypeDTO();

	private static final int SCHOOL_MOCK_ID = -5;

	private static final Functionality FUNCTIONALITY_ONE = new Functionality();

	private static final String TESTSCHOOL5 = "testschool5";

	private static final String SHORT_NOT_EXISTING = "shortNotExisting";

	private static final String TESTSCHOOL = "testschool";

	private static final String SHORT_TESTSCHOOL = "shortTestschool";

	@Autowired
	private SchoolController cut;

	@Autowired
	private CriteriaRepo criteriaRepo;

	@Autowired
	private SchoolRepo schoolRepo;

	@MockBean
	private OsmPOISchoolParser osmParserMock;

	private static final ProjectDTO PRIMARY_PROJECT = new ProjectDTO();

	private static final List<ProjectDTO> DEFAULT_PROJECT_LIST = new ArrayList<>();

	@BeforeAll
	public static void init() {
		PRIMARY_PROJECT.setId(-1);
		DEFAULT_PROJECT_LIST.add(PRIMARY_PROJECT);
		FUNCTIONALITY_ONE.setName("testfunc1");
		FUNCTIONALITY_ONE.setId(-1L);
		SCHOOL_TYPE.setSchoolTypeValue(SchoolTypeValue.GYMNASIUM.getValue());

	}
	
	@Test
	void testFindNotRegisteredSchoolsByNameAdminWithValidName()
			throws ParserConfigurationException, SAXException, IOException, NotFoundException, TransformerException,
			InterruptedException, ExecutionException, OperationNotSupportedException {
		List<FinderEntity> expectedReturnObject = new ArrayList<>();
		List<FinderEntitySearchConstraint> constraints = new ArrayList<>();
		constraints.add(new FinderEntitySearchConstraint("test", ""));
		expectedReturnObject.add(new FinderEntity("test", null, constraints, 1, 2));
		when(osmParserMock.find(Mockito.any(SchoolSearchEntity.class), anyInt())).thenReturn(expectedReturnObject);
		ResponseEntity<List<FinderEntity>> notRegisteredSchoolsByName = cut
				.findNotRegisteredSchoolsByNameAdmin("testSchool", "", 1);
		assertEquals(expectedReturnObject, notRegisteredSchoolsByName.getBody());
	}

	@Test
    void testFindNotRegisteredSchoolsByNameAdminWithNotValidName()
	    throws ParserConfigurationException, SAXException, IOException, NotFoundException, TransformerException,
	    InterruptedException, ExecutionException, OperationNotSupportedException {
	when(osmParserMock.find(Mockito.any(SchoolSearchEntity.class), anyInt())).thenThrow(NotFoundException.class);
	ResponseEntity<List<FinderEntity>> notRegisteredSchoolsByName = cut
		.findNotRegisteredSchoolsByNameAdmin("testSchool", "", 1);
	assertEquals(HttpStatus.NOT_FOUND, notRegisteredSchoolsByName.getStatusCode());
	assertNull(notRegisteredSchoolsByName.getBody());
    }

	@Test
	@Transactional
	void testFindSchoolsByCriteriaExisting() {
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria("test"));
		List<SchoolDTO> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
		assertEquals(2, schoolsByCriteria.size());
		assertFirstSchool(schoolsByCriteria.get(1));
		assertThirdSchool(schoolsByCriteria.get(0));
	}

	@Test
	void testFindSchoolsByCriteriaNotExisting() {
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria("test3"));
		List<SchoolDTO> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
		assertEquals(0, schoolsByCriteria.size());
	}

	@Test
	@Transactional
	void testFindSchoolsByMultipleCriteriasExisting() {
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria("test"));
		criterias.add(new Criteria("test1"));
		List<SchoolDTO> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
		assertEquals(3, schoolsByCriteria.size());
		assertFirstSchool(schoolsByCriteria.get(2));
		assertSecondSchool(schoolsByCriteria.get(1));
		assertThirdSchool(schoolsByCriteria.get(0));
	}

	@Test
	@Transactional
	void testFindSchoolsByMultipleCriteriasOnlyOneExisting() {
		List<Criteria> criterias = new ArrayList<>();
		criterias.add(new Criteria("test"));
		criterias.add(new Criteria("test12"));
		List<SchoolDTO> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
		assertEquals(2, schoolsByCriteria.size());
		assertFirstSchool(schoolsByCriteria.get(1));
		assertThirdSchool(schoolsByCriteria.get(0));
	}

	@Test
	@Transactional
	void testFindAllSchools() {
		List<SchoolDTO> allSchools = cut.findAllSchools();
		assertEquals(3, allSchools.size());
	}

//    @Test
//    void testFindAllSchoolsByInBoundsWrongCriteriaRightBounds() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(1L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221", "2.223",
//		longCriterias, false);
//	assertEquals(0, allSchoolsByInBounds.size());
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsRightCriteriaRightBounds() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221", "2.223",
//		longCriterias, false);
//	assertEquals(1, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(0));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsRightCriteriaWrongBounds() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("2.221", "2.223", "1.110", "1.112",
//		longCriterias, false);
//	assertEquals(0, allSchoolsByInBounds.size());
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsMultipleRightCriteriasOneRightBoundInclusive() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	longCriterias.add(-2L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221", "2.223",
//		longCriterias, false);
//	assertEquals(1, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(0));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsMultipleRightCriteriasLargeRightBoundInclusive() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	longCriterias.add(-2L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("0", "3", "0", "3", longCriterias, false);
//	assertEquals(2, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(1));
//	assertSecondSchool(allSchoolsByInBounds.get(0));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsMultipleRightCriteriasOneRightBoundExclusive() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	longCriterias.add(-2L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("3.332", "3.334", "4.443", "4.445",
//		longCriterias, true);
//	assertEquals(1, allSchoolsByInBounds.size());
//	assertThirdSchool(allSchoolsByInBounds.get(0));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsMultipleRightCriteriasLargeRightBoundExclusive() {
//	List<Long> longCriterias = new ArrayList<>();
//	longCriterias.add(-1L);
//	longCriterias.add(-2L);
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("0", "5", "0", "5", longCriterias, true);
//	assertEquals(1, allSchoolsByInBounds.size());
//	assertThirdSchool(allSchoolsByInBounds.get(0));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsNullCriteriasLargeRightBound() {
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("0", "3", "0", "3", null, false);
//	assertEquals(2, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(0));
//	assertSecondSchool(allSchoolsByInBounds.get(1));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsEmptyCriteriasLargeRightBound() {
//	List<Long> longCriterias = new ArrayList<>();
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("0", "3", "0", "3", longCriterias, false);
//	assertEquals(2, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(0));
//	assertSecondSchool(allSchoolsByInBounds.get(1));
//    }
//
//    @Test
//    void testFindAllSchoolsByInBoundsNullCriteriasRightBounds() {
//	List<SchoolDTO> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.113", "2.221", "2.223", null,
//		false);
//	assertEquals(1, allSchoolsByInBounds.size());
//	assertFirstSchool(allSchoolsByInBounds.get(0));
//    }

	@Test
	@Transactional
	void testFindSchoolDetails() {
		ResponseEntity<SchoolDTO> findFirstSchoolDetails = cut.findSchoolDetails(-1);
		assertFirstSchool(findFirstSchoolDetails.getBody());
	}

	@Test
	void testFindSchoolDetailsNoSchoolFound() {
		ResponseEntity<SchoolDTO> findFirstSchoolDetails = cut.findSchoolDetails(5);
		assertEquals(HttpStatus.NOT_FOUND, findFirstSchoolDetails.getStatusCode());
		assertNull(findFirstSchoolDetails.getBody());
	}

	@Test
	@Transactional
	void testAddNewSchoolAlreadyExisting() {
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, TESTSCHOOL, 21, 12, "", "", null, null,
				null, null, "", null, null, null, null, null, null);
		assertThrows(ResourceAlreadyExistingException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		assertFirstSchool(schoolRepo.getOne(-1L).convertToDTO());
	}

	@Test
	@Transactional
	void testAlterSchoolNotExisting() {
		SchoolDTO newSchool = new SchoolDTO(4, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null, null, null,
				"", null, null, null, null, null, null);
		assertThrows(ResourceNotFoundException.class, () -> {
			cut.alterSchool(newSchool);
		});
		assertFirstSchool(schoolRepo.getOne(-1L).convertToDTO());
	}

	@Test
	void testAddNewSchoolNullPersonsNullCriterias() {
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null,
				null, null, "", null, null, null, null, null, null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
	}

	@Test
	void testAlterSchoolNullPersonsNullCriterias() {
		SchoolDTO newSchool = new SchoolDTO(-2, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null, null, null,
				"", null, null, null, null, null, null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.alterSchool(newSchool);
		});
	}

	@Test
	void testAddNewSchoolNullColor() {
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null,
				null, null, "", null, null, null, null, null, null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
	}

	@Test
	void testAddNewSchoolEmptyColor() {
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null,
				null, null, "", null, null, null, null, null, null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
	}

	@Test
	void testAddNewSchoolColorNotMatchingRegex() {
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null, null,
				null, null, "", null, null, null, null, null, null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
	}

	@Test
	void testAddNewSchoolNullPersonsNotNullCriterias() {
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_NOT_EXISTING, TESTSCHOOL5, 21, 12, "", "", null,
				criterias, null, null, "", null, null, testcriteria, testcriteria, testcriteria, testcriteria);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
	}

	@Test
	void testAlterSchoolNullPersonsNotNullCriterias() {
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		SchoolDTO newSchool = new SchoolDTO(-1, SHORT_TESTSCHOOL, TESTSCHOOL, 21, 12, "", "", null, criterias,
				DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, null, testcriteria, testcriteria, testcriteria,
				testcriteria);
		assertThrows(BadArgumentsException.class, () -> {
			cut.alterSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
	}

	@Test
	void testAddNewSchoolNotNullNotExistingPersonNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(5L);
		personFunctionalityEntity.setPerson(person);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, testschool, 21, 12, "", "",
				personFuncList, criterias, DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, SCHOOL_TYPE, testschool,
				testschool, testschool, testschool);
		assertThrows(ResourceNotFoundException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		assertFalse(allSchools.stream().anyMatch(e -> e.getSchoolName().equals(testschool)));
	}

	@Test
	void testAlterSchoolNotNullNotExistingPersonNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(5L);
		personFunctionalityEntity.setPerson(person);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(-2, SHORT_TESTSCHOOL, testschool, 21, 12, "", "", personFuncList, criterias,
				DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, SCHOOL_TYPE, testschool, testschool, testschool,
				testschool);
		assertThrows(ResourceNotFoundException.class, () -> {
			cut.alterSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		assertFalse(allSchools.stream().anyMatch(e -> e.getSchoolName().equals(testschool)));
	}

	@Test
	@Transactional
	void testAddNewSchoolNotNullExistingPersonNotEmptyFunctionalityNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFunctionalityEntity.setFunctionality(FUNCTIONALITY_ONE);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, testschool, 21, 12, "", "",
				personFuncList, criterias, DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, SCHOOL_TYPE, testschool,
				testschool, testschool, testschool);
		ResponseEntity<SchoolDTO> responseEntity = cut.addNewSchool(newSchool);
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
		assertEquals(1, schoolInDB.count());
		schoolInDB = findMatchingSchool(testschool, allSchools);
		School schoolObject = schoolInDB.findFirst().get();
		assertEquals(-1L, schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
		assertEquals(FUNCTIONALITY_ONE.getId(),
				schoolObject.getPersonSchoolMapping().get(0).getFunctionality().getId());
		assertSchoolEquals(responseEntity.getBody(), newSchool);
	}

	@Test
	@Transactional
	void testAddNewSchoolNoPrimaryProjectSet() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFunctionalityEntity.setFunctionality(FUNCTIONALITY_ONE);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, testschool, 21, 12, "", "",
				personFuncList, criterias, DEFAULT_PROJECT_LIST, null, "", null, null, testschool, testschool,
				testschool, testschool);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		newSchool.setId(-1);
		assertThrows(BadArgumentsException.class, () -> {
			cut.alterSchool(newSchool);
		});
	}

	@Test
	@Transactional
	void testAddNewSchoolPrimaryProjectSetNoGeneralProjectsSet() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFunctionalityEntity.setFunctionality(FUNCTIONALITY_ONE);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, testschool, 21, 12, "", "",
				personFuncList, criterias, null, PRIMARY_PROJECT, "", null, null, testschool, testschool, testschool,
				testschool);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		newSchool.setId(-1);
		assertThrows(BadArgumentsException.class, () -> {
			cut.alterSchool(newSchool);
		});
	}

	@Test
	@Transactional
	void testAlterSchoolNotNullExistingPersonAndAlreadyAddedPersonNotEmptyFunctionalityNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFunctionalityEntity.setFunctionality(FUNCTIONALITY_ONE);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(-3, SHORT_TESTSCHOOL, testschool, 1, 2, "1", "", personFuncList, criterias,
				DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, SCHOOL_TYPE, testschool, testschool, testschool,
				testschool);
		ResponseEntity<SchoolDTO> responseEntity = cut.alterSchool(newSchool);
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
		assertEquals(1, schoolInDB.count());
		schoolInDB = findMatchingSchool(testschool, allSchools);
		School schoolObject = schoolInDB.findFirst().get();
		assertEquals(-1L, schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
		assertEquals(FUNCTIONALITY_ONE.getId(),
				schoolObject.getPersonSchoolMapping().get(0).getFunctionality().getId());
		assertEquals(testschool, schoolObject.getSchoolName());
		assertEquals(3.333, schoolObject.getLatitude().doubleValue(), 0.000001);
		assertEquals(4.444, schoolObject.getLongitude().doubleValue(), 0.000001);
		assertEquals("1", new String(schoolObject.getSchoolPicture()));
		assertEquals(SHORT_TESTSCHOOL, schoolObject.getShortSchoolName());
	}

	@Test
	@Transactional
	void testAlterSchoolNotNullExistingPersonNotEmptyFunctionalityNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-2L);
		personFunctionalityEntity.setPerson(person);
		personFunctionalityEntity.setFunctionality(FUNCTIONALITY_ONE);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(-3, SHORT_TESTSCHOOL, testschool, 1, 2, "1", "", personFuncList, criterias,
				DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, SCHOOL_TYPE, testschool, testschool, testschool,
				testschool);
		ResponseEntity<SchoolDTO> responseEntity = cut.alterSchool(newSchool);
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
		assertEquals(1, schoolInDB.count());
		schoolInDB = findMatchingSchool(testschool, allSchools);
		School schoolObject = schoolInDB.findFirst().get();
		assertEquals(-2L, schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
		assertEquals(FUNCTIONALITY_ONE.getId(),
				schoolObject.getPersonSchoolMapping().get(0).getFunctionality().getId());
		assertEquals(testschool, schoolObject.getSchoolName());
		assertEquals(3.333, schoolObject.getLatitude().doubleValue(), 0.000001);
		assertEquals(4.444, schoolObject.getLongitude().doubleValue(), 0.000001);
		assertEquals("1", new String(schoolObject.getSchoolPicture()));
		assertEquals(SHORT_TESTSCHOOL, schoolObject.getShortSchoolName());

	}

	@Test
	void testAddNewSchoolNotNullExistingPersonEmptyFunctionalityNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(SCHOOL_MOCK_ID, SHORT_TESTSCHOOL, testschool, 21, 12, "", "",
				personFuncList, criterias, DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, null, null, null, null,
				null);
		assertThrows(BadArgumentsException.class, () -> {
			cut.addNewSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		assertFalse(allSchools.stream().anyMatch(e -> e.getSchoolName().equals(testschool)));

	}

	@Test
	void testAlterSchoolNotNullExistingPersonEmptyFunctionalityNotNullCriterias() {
		List<PersonFunctionalityDTO> personFuncList = new ArrayList<>();
		List<Criteria> criterias = new ArrayList<>();
		String testcriteria = "test5";
		criterias.add(new Criteria(testcriteria));
		String testschool = TESTSCHOOL5;
		PersonFunctionalityDTO personFunctionalityEntity = new PersonFunctionalityDTO();
		Person person = new Person();
		person.setId(-1L);
		personFunctionalityEntity.setPerson(person);
		personFuncList.add(personFunctionalityEntity);
		SchoolDTO newSchool = new SchoolDTO(-3, SHORT_TESTSCHOOL, testschool, 1, 2, "1", "", personFuncList, criterias,
				DEFAULT_PROJECT_LIST, PRIMARY_PROJECT, "", null, null, testschool, testschool, testschool, testschool);
		assertThrows(BadArgumentsException.class, () -> {
			cut.alterSchool(newSchool);
		});
		List<Criteria> allCriterias = criteriaRepo.findAll();
		List<School> allSchools = schoolRepo.findAll();
		assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
		assertFalse(allSchools.stream().anyMatch(e -> e.getSchoolName().equals(testschool)));
	}

	private Stream<School> findMatchingSchool(String testschool, List<School> allSchools) {
		return allSchools.stream().filter(e -> (e.getSchoolName().equals(testschool)));
	}

	private void assertFirstSchool(SchoolDTO school) {
		assertEquals(TESTSCHOOL, school.getSchoolName());
		assertEquals(SHORT_TESTSCHOOL, school.getShortSchoolName());
		assertEquals(1.111, school.getLatitude(), 0.001);
		assertEquals(2.222, school.getLongitude(), 0.001);
		assertEquals("image1", school.getSchoolPicture());
		assertEquals("text1", school.getAlternativePictureText());
		assertEquals(-1, school.getPrimaryProject().getId());
	}

	private void assertSchoolEquals(SchoolDTO school, SchoolDTO postbody) {
		assertEquals(postbody.getSchoolName(), school.getSchoolName());
		assertEquals(postbody.getShortSchoolName(), school.getShortSchoolName());
		assertEquals(postbody.getLatitude(), school.getLatitude(), 0.001);
		assertEquals(postbody.getLongitude(), school.getLongitude(), 0.001);
		assertEquals(postbody.getSchoolPicture(), school.getSchoolPicture());
		assertEquals(postbody.getAlternativePictureText(), school.getAlternativePictureText());
	}

	private void assertSecondSchool(SchoolDTO school) {
		assertEquals("testschool2", school.getSchoolName());
		assertEquals("shortTestschool2", school.getShortSchoolName());
		assertEquals(2.222, school.getLatitude(), 0.001);
		assertEquals(1.111, school.getLongitude(), 0.001);
		assertEquals("text2", school.getAlternativePictureText());
		assertEquals("image2", school.getSchoolPicture());
		assertEquals(-1, school.getPrimaryProject().getId());

	}

	private void assertThirdSchool(SchoolDTO school) {
		assertEquals("testschool3", school.getSchoolName());
		assertEquals("shortTestschool3", school.getShortSchoolName());
		assertEquals(3.333, school.getLatitude(), 0.001);
		assertEquals(4.444, school.getLongitude(), 0.001);
		assertEquals("image3", school.getSchoolPicture());
		assertEquals("text3", school.getAlternativePictureText());
		assertEquals(-2, school.getPrimaryProject().getId());

	}
}
