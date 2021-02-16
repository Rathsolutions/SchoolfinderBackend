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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.SchoolController;
import de.rathsolutions.controller.postbody.AddNewSchoolPostbody;
import de.rathsolutions.controller.postbody.AlterSchoolPostbody;
import de.rathsolutions.controller.postbody.PersonFunctionalityEntity;
import de.rathsolutions.controller.postbody.PersonFunctionalityEntity.PersonFunctionality;
import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.CriteriaRepo;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.util.exception.BadArgumentsException;
import de.rathsolutions.util.exception.ResourceAlreadyExistingException;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import de.rathsolutions.util.osm.pojo.OsmPOIEntity;
import de.rathsolutions.util.osm.pojo.SchoolSearchEntity;
import de.rathsolutions.util.osm.specific.OsmPOISchoolParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import javassist.NotFoundException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
public class SchoolControllerTest {

    @Autowired
    private SchoolController cut;

    @Autowired
    private CriteriaRepo criteriaRepo;

    @Autowired
    private SchoolRepo schoolRepo;

    @MockBean
    private OsmPOISchoolParser osmParserMock;

    @Test
    void testFindNotRegisteredSchoolsByNameAdminWithValidName()
            throws ParserConfigurationException, SAXException, IOException, NotFoundException,
            TransformerException, InterruptedException, ExecutionException, OperationNotSupportedException {
        List<OsmPOIEntity> expectedReturnObject = new ArrayList<>();
        expectedReturnObject.add(new OsmPOIEntity("test", null, 1, 2));
        when(osmParserMock.processOsmFile(Mockito.any(SchoolSearchEntity.class), anyInt()))
                .thenReturn(expectedReturnObject);
        ResponseEntity<List<OsmPOIEntity>> notRegisteredSchoolsByName
                = cut.findNotRegisteredSchoolsByNameAdmin("testSchool", "", 1);
        assertEquals(expectedReturnObject, notRegisteredSchoolsByName.getBody());
    }

    @Test
    void testFindNotRegisteredSchoolsByNameAdminWithNotValidName()
            throws ParserConfigurationException, SAXException, IOException, NotFoundException,
            TransformerException, InterruptedException, ExecutionException,
            OperationNotSupportedException {
        when(osmParserMock.processOsmFile(Mockito.any(SchoolSearchEntity.class), anyInt()))
                .thenThrow(NotFoundException.class);
        ResponseEntity<List<OsmPOIEntity>> notRegisteredSchoolsByName
                = cut.findNotRegisteredSchoolsByNameAdmin("testSchool", "", 1);
        assertEquals(HttpStatus.NOT_FOUND, notRegisteredSchoolsByName.getStatusCode());
        assertNull(notRegisteredSchoolsByName.getBody());
    }

    @Test
    void testFindSchoolsByCriteriaExisting() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("test"));
        List<School> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
        assertEquals(2, schoolsByCriteria.size());
        assertFirstSchool(schoolsByCriteria.get(0));
        assertThirdSchool(schoolsByCriteria.get(1));
    }

    @Test
    void testFindSchoolsByCriteriaNotExisting() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("test3"));
        List<School> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
        assertEquals(0, schoolsByCriteria.size());
    }

    @Test
    void testFindSchoolsByMultipleCriteriasExisting() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("test"));
        criterias.add(new Criteria("test1"));
        List<School> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
        assertEquals(3, schoolsByCriteria.size());
        assertFirstSchool(schoolsByCriteria.get(0));
        assertSecondSchool(schoolsByCriteria.get(1));
        assertThirdSchool(schoolsByCriteria.get(2));
    }

    @Test
    void testFindSchoolsByMultipleCriteriasOnlyOneExisting() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("test"));
        criterias.add(new Criteria("test12"));
        List<School> schoolsByCriteria = cut.findSchoolsByCriteria(criterias);
        assertEquals(2, schoolsByCriteria.size());
        assertFirstSchool(schoolsByCriteria.get(0));
        assertThirdSchool(schoolsByCriteria.get(1));
    }

    @Test
    void testFindAllSchools() {
        List<School> allSchools = cut.findAllSchools();
        assertEquals(3, allSchools.size());
    }

    @Test
    void testFindAllSchoolsByInBoundsWrongCriteriaRightBounds() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(1L);
        List<School> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221",
            "2.223", longCriterias, false);
        assertEquals(0, allSchoolsByInBounds.size());
    }

    @Test
    void testFindAllSchoolsByInBoundsRightCriteriaRightBounds() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        List<School> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221",
            "2.223", longCriterias, false);
        assertEquals(1, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
    }

    @Test
    void testFindAllSchoolsByInBoundsRightCriteriaWrongBounds() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        List<School> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("2.221", "2.223", "1.110",
            "1.112", longCriterias, false);
        assertEquals(0, allSchoolsByInBounds.size());
    }

    @Test
    void testFindAllSchoolsByInBoundsMultipleRightCriteriasOneRightBoundInclusive() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        longCriterias.add(1L);
        List<School> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("1.110", "1.112", "2.221",
            "2.223", longCriterias, false);
        assertEquals(1, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
    }

    @Test
    void testFindAllSchoolsByInBoundsMultipleRightCriteriasLargeRightBoundInclusive() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        longCriterias.add(1L);
        List<School> allSchoolsByInBounds
                = cut.findAllSchoolsByInBounds("0", "3", "0", "3", longCriterias, false);
        assertEquals(2, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
        assertSecondSchool(allSchoolsByInBounds.get(1));
    }

    @Test
    void testFindAllSchoolsByInBoundsMultipleRightCriteriasOneRightBoundExclusive() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        longCriterias.add(1L);
        List<School> allSchoolsByInBounds = cut.findAllSchoolsByInBounds("3.332", "3.334", "4.443",
            "4.445", longCriterias, true);
        assertEquals(1, allSchoolsByInBounds.size());
        assertThirdSchool(allSchoolsByInBounds.get(0));
    }

    @Test
    void testFindAllSchoolsByInBoundsMultipleRightCriteriasLargeRightBoundExclusive() {
        List<Long> longCriterias = new ArrayList<>();
        longCriterias.add(0L);
        longCriterias.add(1L);
        List<School> allSchoolsByInBounds
                = cut.findAllSchoolsByInBounds("0", "5", "0", "5", longCriterias, true);
        assertEquals(1, allSchoolsByInBounds.size());
        assertThirdSchool(allSchoolsByInBounds.get(0));
    }

    @Test
    void testFindAllSchoolsByInBoundsNullCriteriasLargeRightBound() {
        List<School> allSchoolsByInBounds
                = cut.findAllSchoolsByInBounds("0", "3", "0", "3", null, false);
        assertEquals(2, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
        assertSecondSchool(allSchoolsByInBounds.get(1));
    }

    @Test
    void testFindAllSchoolsByInBoundsEmptyCriteriasLargeRightBound() {
        List<Long> longCriterias = new ArrayList<>();
        List<School> allSchoolsByInBounds
                = cut.findAllSchoolsByInBounds("0", "3", "0", "3", longCriterias, false);
        assertEquals(2, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
        assertSecondSchool(allSchoolsByInBounds.get(1));
    }

    @Test
    void testFindAllSchoolsByInBoundsNullCriteriasRightBounds() {
        List<School> allSchoolsByInBounds
                = cut.findAllSchoolsByInBounds("1.110", "1.113", "2.221", "2.223", null, false);
        assertEquals(1, allSchoolsByInBounds.size());
        assertFirstSchool(allSchoolsByInBounds.get(0));
    }

    @Test
    void testFindSchoolDetails() {
        ResponseEntity<School> findFirstSchoolDetails = cut.findSchoolDetails(0);
        assertFirstSchool(findFirstSchoolDetails.getBody());
    }

    @Test
    void testFindSchoolDetailsNoSchoolFound() {
        ResponseEntity<School> findFirstSchoolDetails = cut.findSchoolDetails(5);
        assertEquals(HttpStatus.NOT_FOUND, findFirstSchoolDetails.getStatusCode());
        assertNull(findFirstSchoolDetails.getBody());
    }

    @Test
    @Transactional
    void testAddNewSchoolAlreadyExisting() {
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool", 21, 12, "ff0000", "", "", null, null);
        assertThrows(ResourceAlreadyExistingException.class, () -> {
            cut.addNewSchool(newSchool);
        });
        assertFirstSchool(schoolRepo.getOne(0L));
    }

    @Test
    @Transactional
    void testAlterSchoolNotExisting() {
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(4, "testschool5", 21, 12, "", "", "ff0000", null, null);
        assertThrows(ResourceNotFoundException.class, () -> {
            cut.alterSchool(newSchool);
        });
        assertFirstSchool(schoolRepo.getOne(0L));
    }

    @Test
    void testAddNewSchoolNullPersonsNullCriterias() {
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool5", 21, 12, "ff0000", "", "", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.addNewSchool(newSchool);
        });
    }

    @Test
    void testAlterSchoolNullPersonsNullCriterias() {
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(1, "testschool5", 21, 12, "", "", "ff0000", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.alterSchool(newSchool);
        });
    }

    @Test
    void testAddNewSchoolNullColor() {
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool5", 21, 12, null, "", "", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.addNewSchool(newSchool);
        });
    }

    @Test
    void testAlterSchoolNullColor() {
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(1, "testschool5", 21, 12, "", "", null, null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.alterSchool(newSchool);
        });
    }

    @Test
    void testAddNewSchoolEmptyColor() {
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool5", 21, 12, "", "", "", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.addNewSchool(newSchool);
        });
    }

    @Test
    void testAlterSchoolEmptyColor() {
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(1, "testschool5", 21, 12, "", "", "", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.alterSchool(newSchool);
        });
    }
    @Test
    void testAddNewSchoolColorNotMatchingRegex() {
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool5", 21, 12, "", "", "schoolfinder", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.addNewSchool(newSchool);
        });
    }
    @Test
    void testAlterSchoolColorNotMatchingHexRegex() {
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(1, "testschool5", 21, 12, "", "", "schoolfinder", null, null);
        assertThrows(BadArgumentsException.class, () -> {
            cut.alterSchool(newSchool);
        });
    }
    @Test
    void testAddNewSchoolNullPersonsNotNullCriterias() {
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        AddNewSchoolPostbody newSchool
                = new AddNewSchoolPostbody("testschool5", 21, 12, "ff0000", "", "", null, criterias);
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
        AlterSchoolPostbody newSchool
                = new AlterSchoolPostbody(1, "testschool", 21, 12, "", "", "ff0000", null, criterias);
        assertThrows(BadArgumentsException.class, () -> {
            cut.alterSchool(newSchool);
        });
        List<Criteria> allCriterias = criteriaRepo.findAll();
        assertFalse(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
    }

    @Test
    void testAddNewSchoolNotNullNotExistingPersonNotNullCriterias() {
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(5L);
        personFunctionalityEntity.setPerson(person);
        personFuncList.add(personFunctionalityEntity);
        AddNewSchoolPostbody newSchool = new AddNewSchoolPostbody(testschool, 21, 12, "ff0000", "", "",
                personFuncList, criterias);
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
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(5L);
        personFunctionalityEntity.setPerson(person);
        personFuncList.add(personFunctionalityEntity);
        AlterSchoolPostbody newSchool = new AlterSchoolPostbody(1, testschool, 21, 12, "", "",
                "ff0000", personFuncList, criterias);
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
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(0L);
        personFunctionalityEntity.setPerson(person);
        personFunctionalityEntity.setFunctionality(PersonFunctionality.XR);
        personFuncList.add(personFunctionalityEntity);
        AddNewSchoolPostbody newSchool = new AddNewSchoolPostbody(testschool, 21, 12, "ff0000", "", "",
                personFuncList, criterias);
        ResponseEntity<School> responseEntity = cut.addNewSchool(newSchool);
        List<Criteria> allCriterias = criteriaRepo.findAll();
        List<School> allSchools = schoolRepo.findAll();
        assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
        Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
        assertEquals(1, schoolInDB.count());
        schoolInDB = findMatchingSchool(testschool, allSchools);
        School schoolObject = schoolInDB.findFirst().get();
        assertEquals(0L,
            schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
        assertEquals(PersonFunctionality.XR,
            schoolObject.getPersonSchoolMapping().get(0).getFunctionality());

    }

    @Test
    @Transactional
    void testAlterSchoolNotNullExistingPersonAndAlreadyAddedPersonNotEmptyFunctionalityNotNullCriterias() {
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(0L);
        personFunctionalityEntity.setPerson(person);
        personFunctionalityEntity.setFunctionality(PersonFunctionality.XR);
        personFuncList.add(personFunctionalityEntity);
        AlterSchoolPostbody newSchool = new AlterSchoolPostbody(2, testschool, 1, 2, "1", "", "ff0000",
                personFuncList, criterias);
        ResponseEntity<School> responseEntity = cut.alterSchool(newSchool);
        List<Criteria> allCriterias = criteriaRepo.findAll();
        List<School> allSchools = schoolRepo.findAll();
        assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
        Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
        assertEquals(1, schoolInDB.count());
        schoolInDB = findMatchingSchool(testschool, allSchools);
        School schoolObject = schoolInDB.findFirst().get();
        assertEquals(0L,
            schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
        assertEquals(PersonFunctionality.XR,
            schoolObject.getPersonSchoolMapping().get(0).getFunctionality());
        assertEquals(testschool, schoolObject.getSchoolName());
        assertEquals(3.333, schoolObject.getLatitude().doubleValue(), 0.000001);
        assertEquals(4.444, schoolObject.getLongitude().doubleValue(), 0.000001);
        assertEquals("1", schoolObject.getSchoolPicture());
    }

    @Test
    @Transactional
    void testAlterSchoolNotNullExistingPersonNotEmptyFunctionalityNotNullCriterias() {
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(1L);
        personFunctionalityEntity.setPerson(person);
        personFunctionalityEntity.setFunctionality(PersonFunctionality.XR);
        personFuncList.add(personFunctionalityEntity);
        AlterSchoolPostbody newSchool = new AlterSchoolPostbody(2, testschool, 1, 2, "1", "", "ff0000",
                personFuncList, criterias);
        ResponseEntity<School> responseEntity = cut.alterSchool(newSchool);
        List<Criteria> allCriterias = criteriaRepo.findAll();
        List<School> allSchools = schoolRepo.findAll();
        assertTrue(allCriterias.stream().anyMatch(e -> e.getCriteriaName().equals(testcriteria)));
        Stream<School> schoolInDB = findMatchingSchool(testschool, allSchools);
        assertEquals(1, schoolInDB.count());
        schoolInDB = findMatchingSchool(testschool, allSchools);
        School schoolObject = schoolInDB.findFirst().get();
        assertEquals(1L,
            schoolObject.getPersonSchoolMapping().get(0).getPerson().getId().longValue());
        assertEquals(PersonFunctionality.XR,
            schoolObject.getPersonSchoolMapping().get(0).getFunctionality());
        assertEquals(testschool, schoolObject.getSchoolName());
        assertEquals(3.333, schoolObject.getLatitude().doubleValue(), 0.000001);
        assertEquals(4.444, schoolObject.getLongitude().doubleValue(), 0.000001);
        assertEquals("1", schoolObject.getSchoolPicture());
    }

    @Test
    void testAddNewSchoolNotNullExistingPersonEmptyFunctionalityNotNullCriterias() {
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(0L);
        personFunctionalityEntity.setPerson(person);
        personFuncList.add(personFunctionalityEntity);
        AddNewSchoolPostbody newSchool = new AddNewSchoolPostbody(testschool, 21, 12, "", "", "ff0000",
                personFuncList, criterias);
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
        List<PersonFunctionalityEntity> personFuncList = new ArrayList<>();
        List<Criteria> criterias = new ArrayList<>();
        String testcriteria = "test5";
        criterias.add(new Criteria(testcriteria));
        String testschool = "testschool5";
        PersonFunctionalityEntity personFunctionalityEntity = new PersonFunctionalityEntity();
        Person person = new Person();
        person.setId(0L);
        personFunctionalityEntity.setPerson(person);
        personFuncList.add(personFunctionalityEntity);
        AlterSchoolPostbody newSchool = new AlterSchoolPostbody(2, testschool, 1, 2, "1", "", "ff0000",
                personFuncList, criterias);
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

    private void assertFirstSchool(School school) {
        assertEquals("testschool", school.getSchoolName());
        assertEquals(1.111, school.getLatitude().doubleValue(), 0.001);
        assertEquals(2.222, school.getLongitude().doubleValue(), 0.001);
        assertEquals("0x00", school.getSchoolPicture());
        assertEquals("ff0000", school.getColor());
    }

    private void assertSecondSchool(School school) {
        assertEquals("testschool2", school.getSchoolName());
        assertEquals(2.222, school.getLatitude().doubleValue(), 0.001);
        assertEquals(1.111, school.getLongitude().doubleValue(), 0.001);
        assertEquals("0xFF", school.getSchoolPicture());
    }

    private void assertThirdSchool(School school) {
        assertEquals("testschool3", school.getSchoolName());
        assertEquals(3.333, school.getLatitude().doubleValue(), 0.001);
        assertEquals(4.444, school.getLongitude().doubleValue(), 0.001);
        assertEquals("0xAF", school.getSchoolPicture());
    }
}
