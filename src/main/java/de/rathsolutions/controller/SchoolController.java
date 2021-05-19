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

import de.rathsolutions.controller.postbody.AddNewSchoolPostbody;
import de.rathsolutions.controller.postbody.AlterSchoolPostbody;
import de.rathsolutions.controller.postbody.PersonFunctionalityEntity.PersonFunctionality;
import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.jpa.entity.PersonSchoolMapping;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.CriteriaRepo;
import de.rathsolutions.jpa.repo.PersonRepo;
import de.rathsolutions.jpa.repo.PersonSchoolMappingRepo;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.util.exception.BadArgumentsException;
import de.rathsolutions.util.exception.ResourceAlreadyExistingException;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import de.rathsolutions.util.osm.pojo.OsmPOIEntity;
import de.rathsolutions.util.osm.pojo.SchoolSearchEntity;
import de.rathsolutions.util.osm.specific.OsmPOICityOnlyParser;
import de.rathsolutions.util.osm.specific.OsmPOISchoolParser;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javassist.NotFoundException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/api/v1/schools")
public class SchoolController {

    private static final String COLOR_CODE_REGEX = "^[0-9A-Fa-f]{6}$";

    @Autowired
    private SchoolRepo schoolRepo;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private CriteriaRepo criteriaRepo;

    @Autowired
    private OsmPOISchoolParser osmSchoolParser;

    @Autowired
    private PersonSchoolMappingRepo personSchoolMappingRepo;

    @Operation(summary = "searches non-registered school resources by their name in an osm document. This schools must not be registered within the application")
    @GetMapping("/search/findNotRegisteredSchoolsByName")
    public ResponseEntity<List<OsmPOIEntity>> findNotRegisteredSchoolsByNameAdmin(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String city,
            @RequestParam(defaultValue = "1") int amount) {
        try {
            List<OsmPOIEntity> resultsByName;
            System.out.println(name);
            resultsByName
                    = osmSchoolParser.processOsmFile(new SchoolSearchEntity(name, city), amount);
            return ResponseEntity.ok()
                    .header("Copyright", "This list was generated using Open Street Maps Data")
                    .body(resultsByName);
        } catch (ParserConfigurationException | SAXException | IOException | NotFoundException
                | TransformerException | InterruptedException | ExecutionException
                | OperationNotSupportedException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "searches registered school resources by a list of criterias in the database")
    @GetMapping("/search/findSchoolsByCriteria")
    public List<School> findSchoolsByCriteria(List<Criteria> criterias) {
        List<Criteria> criteriasFromDb = new ArrayList<>();
        criterias.stream().forEach(
            e -> criteriasFromDb.add(criteriaRepo.findByCriteriaName(e.getCriteriaName())));
        return schoolRepo.findDistinctByMatchingCriteriasIn(criteriasFromDb);
    }

    @Operation(summary = "searches all school resources")
    @GetMapping("/search/findAllSchools")
    public List<School> findAllSchools() {
        return schoolRepo.findAll();
    }
    
    @Operation(summary = "searches all school resources ordered by their name")
    @GetMapping("/search/findAllSchoolsOrderedByName")
    public List<School> findAllSchoolsOrderByName() {
        return schoolRepo.findAllByOrderBySchoolName();
    }

    @Operation(summary = "searches all school resources within latlong boundaries")
    @GetMapping("/search/findAllSchoolsInBounds")
    @Transactional
    public List<School> findAllSchoolsByInBounds(String leftLatBound, String rightLatBound,
            String topLongBound, String bottomLongBound,
            @RequestParam(value = "criteriaNumbers", required = false) List<Long> criteriaNumbers,
            @RequestParam(value = "exclusiveSearch", required = false, defaultValue = "false") boolean exclusiveSearch) {
        Double leftLat = Double.valueOf(leftLatBound);
        Double rightLat = Double.valueOf(rightLatBound);
        Double topLong = Double.valueOf(topLongBound);
        Double bottomLong = Double.valueOf(bottomLongBound);
        if (criteriaNumbers != null && criteriaNumbers.size() > 0) {
            List<Criteria> criterias = criteriaRepo.findAllByIdIn(criteriaNumbers);
            List<School> allSchoolsMatching = schoolRepo
                    .findDistinctByLatitudeBetweenAndLongitudeBetweenAndMatchingCriteriasIn(leftLat,
                        rightLat, topLong, bottomLong, criterias);
            if (!exclusiveSearch) {
                return allSchoolsMatching;
            } else {
                return allSchoolsMatching.stream()
                        .filter(e -> e.getMatchingCriterias().containsAll(criterias))
                        .collect(Collectors.toList());
            }
        }
        List<School> allByLatitudeBetweenAndLongitudeBetween
                = schoolRepo.findAllByLatitudeBetweenAndLongitudeBetween(leftLat, rightLat, topLong,
                    bottomLong);
        return allByLatitudeBetweenAndLongitudeBetween;
    }

    @Operation(summary = "searches a school resource by id with all details")
    @GetMapping("/search/findSchoolDetails")
    public ResponseEntity<School> findSchoolDetails(long id) {
        Optional<School> schoolByIdOptional = schoolRepo.findById(id);
        if (schoolByIdOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        School schoolById = schoolByIdOptional.get();
        return ResponseEntity.ok(schoolById);
    }

    @Operation(summary = "creates a new school resource")
    @PutMapping("/create/addNewSchool")
    @Transactional
    public ResponseEntity<School> addNewSchool(
            @RequestBody AddNewSchoolPostbody addNewSchoolPostbody) {
        Optional<School> alreadyExistingSchool
                = schoolRepo.findOneBySchoolName(addNewSchoolPostbody.getSchoolName());
        if (alreadyExistingSchool.isPresent()) {
            throw new ResourceAlreadyExistingException(alreadyExistingSchool.get());
        }
        if (isSchoolPostbodyNotValid(addNewSchoolPostbody)) {
            throw new BadArgumentsException(addNewSchoolPostbody);
        }
        List<Criteria> allMatchingSchoolCriterias
                = generateMatchingSchoolCriteriasAndPersistIfNotExisting(addNewSchoolPostbody);
        School school = new School(addNewSchoolPostbody.getSchoolName(),
                addNewSchoolPostbody.getLatitude(), addNewSchoolPostbody.getLongitude(),
                allMatchingSchoolCriterias);
        school.setSchoolPicture(addNewSchoolPostbody.getSchoolPicture());
        school.setAlternativePictureText(addNewSchoolPostbody.getAlternativePictureText());
        school.setArContent(addNewSchoolPostbody.getArContent());
        school.setMakerspaceContent(addNewSchoolPostbody.getMakerspaceContent());
        school.setColor(addNewSchoolPostbody.getColor());
        fillPersonSchoolMappingOfSchool(addNewSchoolPostbody, school);
        return ResponseEntity.ok(schoolRepo.save(school));
    }

    @Operation(summary = "alterates a already existing school resource")
    @PatchMapping("/edit/alterSchool")
    @Transactional
    public ResponseEntity<School> alterSchool(
            @RequestBody AlterSchoolPostbody alterSchoolPostbody) {
        Optional<School> alreadyExistingSchool = schoolRepo.findById(alterSchoolPostbody.getId());
        if (alreadyExistingSchool.isEmpty()) {
            throw new ResourceNotFoundException(alterSchoolPostbody, "school");
        }
        School matchingSchool = alreadyExistingSchool.get();
        List<Criteria> allMatchingSchoolCriterias
                = generateMatchingSchoolCriteriasAndPersistIfNotExisting(alterSchoolPostbody);
        if (isSchoolPostbodyNotValid(alterSchoolPostbody)) {
            throw new BadArgumentsException(alterSchoolPostbody);
        }
        matchingSchool.setSchoolName(alterSchoolPostbody.getSchoolName());
//        matchingSchool.setLatitude(alterSchoolPostbody.getLatitude());
//        matchingSchool.setLongitude(alterSchoolPostbody.getLongitude());
        matchingSchool.setMatchingCriterias(allMatchingSchoolCriterias);
        matchingSchool.setSchoolPicture(alterSchoolPostbody.getSchoolPicture());
        matchingSchool.setAlternativePictureText(alterSchoolPostbody.getAlternativePictureText());
        matchingSchool.setArContent(alterSchoolPostbody.getArContent());
        matchingSchool.setMakerspaceContent(alterSchoolPostbody.getMakerspaceContent());
        matchingSchool.setColor(alterSchoolPostbody.getColor());
        matchingSchool.getPersonSchoolMapping().clear();
        fillPersonSchoolMappingOfSchool(alterSchoolPostbody, matchingSchool);
        return ResponseEntity.ok(schoolRepo.save(matchingSchool));
    }

    @GetMapping("/search/findPersonFunctionalityForPersonAndSchoolAndFunctionality")
    public ResponseEntity<Long> getPersonFunctionalityIDForPersonAndSchoolAndFunctionality(
            long personId, long schoolId, String functionality) {
        Optional<School> school = schoolRepo.findById(schoolId);
        Optional<Person> person = personRepo.findById(personId);
        if (person.isEmpty()) {
            throw new ResourceNotFoundException(personId, "Person could not be found");
        } else if (school.isEmpty()) {
            throw new ResourceNotFoundException(schoolId, "School could not be found");
        }
        Optional<PersonSchoolMapping> personSchoolMapping
                = personSchoolMappingRepo.findOneBySchoolAndPersonAndFunctionality(school.get(),
                    person.get(), PersonFunctionality.valueOf(functionality.toUpperCase()));
        if (personSchoolMapping.isEmpty()) {
            throw new ResourceNotFoundException(person.toString(),
                    "The person has no mapping to school " + school.toString());
        }
        return ResponseEntity.ok(personSchoolMapping.get().getId());
    }

    @Operation(summary = "deletes a school resource")
    @DeleteMapping("/delete/deleteSchool")
    public ResponseEntity<School> deleteSchool(long schoolId) {
        schoolRepo.deleteById(schoolId);
        return ResponseEntity.ok().build();
    }

    private void fillPersonSchoolMappingOfSchool(AddNewSchoolPostbody alterSchoolPostbody,
            School matchingSchool) {

        alterSchoolPostbody.getPersonSchoolMapping().forEach(e -> {
            Optional<Person> personById = personRepo.findById(e.getPerson().getId());
            if (personById.isEmpty()) {
                throw new ResourceNotFoundException(alterSchoolPostbody, "person");
            }
            if (e.getFunctionality() == null) {
                throw new BadArgumentsException(e);
            }
            PersonSchoolMapping personSchoolMapping = new PersonSchoolMapping(personById.get(),
                    matchingSchool, e.getFunctionality());
            matchingSchool.getPersonSchoolMapping().add(personSchoolMapping);
        });
    }

    private List<Criteria> generateMatchingSchoolCriteriasAndPersistIfNotExisting(
            AddNewSchoolPostbody alterSchoolPostbody) {
        List<Criteria> allMatchingSchoolCriterias = new ArrayList<>();
        if (alterSchoolPostbody.getMatchingCriterias() != null) {
            allMatchingSchoolCriterias = criteriaRepo
                    .findAllByCriteriaNameIn(alterSchoolPostbody.getMatchingCriterias().stream()
                            .map(Criteria::getCriteriaName).collect(Collectors.toList()));
            List<Criteria> listForStream = allMatchingSchoolCriterias;
            alterSchoolPostbody.getMatchingCriterias().forEach(e -> {
                if (!listForStream.stream()
                        .anyMatch(f -> f.getCriteriaName().equalsIgnoreCase(e.getCriteriaName()))) {
                    System.out.println(e.getCriteriaName().length());
                    Criteria savedCrit = criteriaRepo.save(new Criteria(e.getCriteriaName()));
                    listForStream.add(savedCrit);
                }
            });
        }
        return allMatchingSchoolCriterias;
    }

    private boolean isSchoolPostbodyNotValid(AddNewSchoolPostbody addNewSchoolPostbody) {
        return addNewSchoolPostbody.getPersonSchoolMapping() == null
                || addNewSchoolPostbody.getPersonSchoolMapping().isEmpty()
                || addNewSchoolPostbody.getColor() == null
                || addNewSchoolPostbody.getColor().isEmpty()
                || !addNewSchoolPostbody.getColor().matches(COLOR_CODE_REGEX);
    }
}
