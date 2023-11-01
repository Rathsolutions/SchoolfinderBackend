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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.locationtech.jts.geom.Point;
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
import org.webjars.NotFoundException;
import org.xml.sax.SAXException;

import de.rathsolutions.controller.postbody.ProjectDTO;
import de.rathsolutions.controller.postbody.SchoolDTO;
import de.rathsolutions.jpa.entity.Area;
import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.entity.Functionality;
import de.rathsolutions.jpa.entity.Person;
import de.rathsolutions.jpa.entity.PersonSchoolMapping;
import de.rathsolutions.jpa.entity.Project;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.entity.SchoolType;
import de.rathsolutions.jpa.entity.SchoolTypeValue;
import de.rathsolutions.jpa.entity.additional.AdditionalInformation;
import de.rathsolutions.jpa.entity.additional.InformationType;
import de.rathsolutions.jpa.repo.AdditionalInformationRepo;
import de.rathsolutions.jpa.repo.AreaRepository;
import de.rathsolutions.jpa.repo.CriteriaRepo;
import de.rathsolutions.jpa.repo.FunctionalityRepo;
import de.rathsolutions.jpa.repo.InformationTypeRepo;
import de.rathsolutions.jpa.repo.PersonRepo;
import de.rathsolutions.jpa.repo.PersonSchoolMappingRepo;
import de.rathsolutions.jpa.repo.ProjectRepo;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.jpa.repo.SchoolTypeRepo;
import de.rathsolutions.util.GeometryUtils;
import de.rathsolutions.util.exception.BadArgumentsException;
import de.rathsolutions.util.exception.ResourceAlreadyExistingException;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.SchoolSearchEntity;
import de.rathsolutions.util.finder.specific.osm.OsmPOISchoolParser;
import de.rathsolutions.util.structure.internalFinder.InstitutionAttributeFinderEntries;
import io.swagger.v3.oas.annotations.Operation;

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

	@Autowired
	private ProjectRepo projectRepo;

	@Autowired
	private FunctionalityRepo functionalityRepo;

	@Autowired
	private AreaRepository areaRepo;

	@Autowired
	private AdditionalInformationRepo additionalInformationRepo;

	@Autowired
	private InformationTypeRepo informationTypeRepo;

	@Autowired
	private SchoolTypeRepo schoolTypeRepo;
	
	@Autowired
	private InstitutionAttributeFinderEntries finderEntries;

	@Operation(summary = "searches non-registered school resources by their name in an osm document. This schools must not be registered within the application")
	@GetMapping("/search/findNotRegisteredSchoolsByName")
	public ResponseEntity<List<FinderEntity>> findNotRegisteredSchoolsByNameAdmin(
			@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "") String city,
			@RequestParam(defaultValue = "1") int amount) {
		try {
			List<FinderEntity> resultsByName;
			System.out.println(name);
			resultsByName = osmSchoolParser.find(new SchoolSearchEntity(name, city), amount);
			return ResponseEntity.ok().header("Copyright", "This list was generated using Open Street Maps Data")
					.body(resultsByName);
		} catch (ParserConfigurationException | SAXException | IOException | NotFoundException | TransformerException
				| InterruptedException | ExecutionException | OperationNotSupportedException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = "searches registered school resources by a list of criterias in the database")
	@GetMapping("/search/findSchoolsByCriteria")
	public List<SchoolDTO> findSchoolsByCriteria(List<Criteria> criterias) {
		List<Criteria> criteriasFromDb = new ArrayList<>();
		criterias.stream().forEach(e -> criteriasFromDb.add(criteriaRepo.findByCriteriaName(e.getCriteriaName())));
		return schoolRepo.findDistinctByMatchingCriteriasIn(criteriasFromDb).stream().map(e -> e.convertToDTO())
				.collect(Collectors.toList());
	}

	@Operation(summary = "searches all school resources")
	@GetMapping("/search/findAllSchools")
	public List<SchoolDTO> findAllSchools() {
		List<SchoolDTO> allSchools = schoolRepo.findAll().stream().map(e -> e.convertToDTO())
				.collect(Collectors.toList());
		allSchools.forEach(e -> {
			Point locationPoint = GeometryUtils.createPoint(e.getLatitude(), e.getLongitude());
			locationPoint.setSRID(4326);
			List<Area> areasContainingPoint = areaRepo.findAreasContainingPoint(locationPoint);
			if (!areasContainingPoint.isEmpty()) {
				e.setCorrespondingAreaName(areasContainingPoint.get(0).getName());
			}
		});
		return allSchools;
	}

	@Operation(summary = "searches all school resources ordered by their name")
	@GetMapping("/search/findAllSchoolsOrderedByName")
	public List<SchoolDTO> findAllSchoolsOrderByName() {
		return schoolRepo.findAllByOrderBySchoolName().stream().map(e -> e.convertToShrinkedDTO()).collect(Collectors.toList());
	}

	private List<School> findAllSchoolsByInBoundsInternal(String leftLatBound, String rightLatBound,
			String topLongBound, String bottomLongBound, List<Long> criteriaNumbers, List<Integer> schoolTypeIds,
			boolean exclusiveSearch) {
		Double leftLat = Double.valueOf(leftLatBound);
		Double rightLat = Double.valueOf(rightLatBound);
		Double topLong = Double.valueOf(topLongBound);
		Double bottomLong = Double.valueOf(bottomLongBound);
		List<SchoolType> schoolTypes = null;
		if (schoolTypeIds != null && schoolTypeIds.size() > 0) {
			schoolTypes = schoolTypeRepo.findAllByIdIn(schoolTypeIds);
		}
		List<Criteria> criterias = null;
		if (criteriaNumbers != null && criteriaNumbers.size() > 0) {
			criterias = criteriaRepo.findAllByIdIn(criteriaNumbers);
		}
		final List<Criteria> finalCriterias = criterias;
		List<School> allSchoolsMatching;
		if (listConditionMet(schoolTypes) && listConditionMet(criterias)) {
			allSchoolsMatching = schoolRepo
					.findDistinctByLatitudeBetweenAndLongitudeBetweenAndMatchingCriteriasInAndTypeIn(leftLat, rightLat,
							topLong, bottomLong, criterias, schoolTypes);
			if (exclusiveSearch) {
				allSchoolsMatching = allSchoolsMatching.stream()
						.filter(e -> e.getMatchingCriterias().containsAll(finalCriterias)).collect(Collectors.toList());
			}
		} else if (listConditionMet(schoolTypes) && !listConditionMet(criterias)) {
			allSchoolsMatching = schoolRepo.findDistinctByLatitudeBetweenAndLongitudeBetweenAndTypeIn(leftLat, rightLat,
					topLong, bottomLong, schoolTypes);
		} else if (!listConditionMet(schoolTypes) && listConditionMet(criterias)) {
			allSchoolsMatching = schoolRepo.findDistinctByLatitudeBetweenAndLongitudeBetweenAndMatchingCriteriasIn(
					leftLat, rightLat, topLong, bottomLong, criterias);
			if (exclusiveSearch) {
				allSchoolsMatching = allSchoolsMatching.stream()
						.filter(e -> e.getMatchingCriterias().containsAll(finalCriterias)).collect(Collectors.toList());
			}
		} else {
			allSchoolsMatching = schoolRepo.findAllByLatitudeBetweenAndLongitudeBetween(leftLat, rightLat, topLong,
					bottomLong);
		}
		return allSchoolsMatching;
	}

	private boolean listConditionMet(List schoolTypes) {
		return schoolTypes != null && !schoolTypes.isEmpty();
	}

	@Operation(summary = "searches all school resources within latlong boundaries")
	@GetMapping("/search/findAllSchoolsInBoundsHavingCriteriasAndProject")
	@Transactional(readOnly = true)
	public ResponseEntity<List<SchoolDTO>> findAllSchoolsInBoundsHavingCriteriasAndProject(String leftLatBound,
			String rightLatBound, String topLongBound, String bottomLongBound,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "criteriaNumbers", required = false) List<Long> criteriaNumbers,
			@RequestParam(value = "schoolTypeIds", required = false) List<Integer> schoolTypeIds,
			@RequestParam(value = "exclusiveSearch", required = false, defaultValue = "false") boolean exclusiveSearch) {
		List<School> matchingSchools = this.findAllSchoolsByInBoundsInternal(leftLatBound, rightLatBound, topLongBound,
				bottomLongBound, criteriaNumbers, schoolTypeIds, exclusiveSearch);
		if (projectId == null) {
			return ResponseEntity
					.ok(matchingSchools.stream().map(e -> e.convertToShrinkedDTO()).collect(Collectors.toList()));
		}
		return ResponseEntity.ok(matchingSchools.stream()
				.filter(e -> e.getProjects().stream().anyMatch(f -> f.getId() == projectId.longValue()))
				.map(e -> e.convertToShrinkedDTO()).collect(Collectors.toList()));
	}

	/**
	 * This method acts as a wrapper for the parent method which resolves all
	 * schools for the angular frontend. To reduce the transfered data amount, the
	 * project icons are removed as they should only be fetched once and added by
	 * the frontend
	 * 
	 * @param leftLatBound
	 * @param rightLatBound
	 * @param topLongBound
	 * @param bottomLongBound
	 * @param projectId
	 * @param criteriaNumbers
	 * @param schoolTypeIds
	 * @param exclusiveSearch
	 * @return
	 */
	@Operation(summary = "searches all school resources within latlong boundaries")
	@GetMapping("/search/findAllSchoolsInBoundsHavingCriteriasAndProjectWithoutProjectIconInResponse")
	@Transactional(readOnly = true)
	public ResponseEntity<List<SchoolDTO>> findAllSchoolsInBoundsHavingCriteriasAndProjectWithoutProjectIconInResponse(
			String leftLatBound, String rightLatBound, String topLongBound, String bottomLongBound,
			@RequestParam(value = "projectId", required = false) Long projectId,
			@RequestParam(value = "criteriaNumbers", required = false) List<Long> criteriaNumbers,
			@RequestParam(value = "schoolTypeIds", required = false) List<Integer> schoolTypeIds,
			@RequestParam(value = "exclusiveSearch", required = false, defaultValue = "false") boolean exclusiveSearch) {
		ResponseEntity<List<SchoolDTO>> findAllSchoolsInBoundsHavingCriteriasAndProject = findAllSchoolsInBoundsHavingCriteriasAndProject(
				leftLatBound, rightLatBound, topLongBound, bottomLongBound, projectId, criteriaNumbers, schoolTypeIds,
				exclusiveSearch);
		List<SchoolDTO> schoolDtoBody = findAllSchoolsInBoundsHavingCriteriasAndProject.getBody();
		schoolDtoBody.forEach(e -> {
			e.setPrimaryProject(e.getPrimaryProject().convertToShrinkedDto());
			e.setProjects(e.getProjects().stream().map(project -> project.convertToShrinkedDto())
					.collect(Collectors.toList()));
		});
		return findAllSchoolsInBoundsHavingCriteriasAndProject;
	}

	@Operation(summary = "searches a school resource by id with all details")
	@GetMapping("/search/findSchoolDetails")
	public ResponseEntity<SchoolDTO> findSchoolDetails(long id) {
		Optional<School> schoolByIdOptional = schoolRepo.findById(id);
		if (schoolByIdOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		School schoolById = schoolByIdOptional.get();
		return ResponseEntity.ok(schoolById.convertToDTO());
	}

	@Operation(summary = "creates a new school resource")
	@PutMapping("/create/addNewSchool")
	@Transactional
	public ResponseEntity<SchoolDTO> addNewSchool(@RequestBody SchoolDTO addNewSchoolPostbody) {
		Optional<School> alreadyExistingSchool = schoolRepo.findOneBySchoolName(addNewSchoolPostbody.getSchoolName());
		if (alreadyExistingSchool.isPresent()) {
			throw new ResourceAlreadyExistingException(alreadyExistingSchool.get());
		}
		List<Project> allFoundProjects;
		try {
			allFoundProjects = generateProjectEntityListForSchoolPostbody(addNewSchoolPostbody);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
		List<Criteria> allMatchingSchoolCriterias = generateMatchingSchoolCriteriasAndPersistIfNotExisting(
				addNewSchoolPostbody);
		School school = new School();
		school.setLatitude(addNewSchoolPostbody.getLatitude());
		school.setLongitude(addNewSchoolPostbody.getLongitude());
		fillSchoolPostbodyWithAllInformation(addNewSchoolPostbody, school, allFoundProjects,
				allMatchingSchoolCriterias);
		School savedSchool = schoolRepo.save(school);
		finderEntries.clear();
		finderEntries.buildEntryList();
		return ResponseEntity.ok(savedSchool.convertToDTO());
	}

	@Operation(summary = "alterates an already existing school resource")
	@PatchMapping("/edit/alterSchool")
	@Transactional
	public ResponseEntity<SchoolDTO> alterSchool(@RequestBody SchoolDTO alterSchoolPostbody) {
		Optional<School> alreadyExistingSchool = schoolRepo.findById(alterSchoolPostbody.getId());
		if (alreadyExistingSchool.isEmpty()) {
			throw new ResourceNotFoundException(alterSchoolPostbody, "school");
		}
		School matchingSchool = alreadyExistingSchool.get();
		List<Project> allFoundProjects;
		try {
			allFoundProjects = generateProjectEntityListForSchoolPostbody(alterSchoolPostbody);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
		List<Criteria> allMatchingSchoolCriterias = generateMatchingSchoolCriteriasAndPersistIfNotExisting(
				alterSchoolPostbody);
		fillSchoolPostbodyWithAllInformation(alterSchoolPostbody, matchingSchool, allFoundProjects,
				allMatchingSchoolCriterias);
		School updatedSchool = schoolRepo.save(matchingSchool);
		finderEntries.clear();
		finderEntries.buildEntryList();
		return ResponseEntity.ok(updatedSchool.convertToDTO());
	}

	private void fillSchoolPostbodyWithAllInformation(SchoolDTO schoolPostbody, School schoolEntity,
			List<Project> allFoundProjects, List<Criteria> allMatchingSchoolCriterias) {
		if (isSchoolPostbodyNotValid(schoolPostbody)) {
			throw new BadArgumentsException(schoolPostbody);
		}
		addPrimaryProjectToSchoolPostbody(schoolPostbody, schoolEntity, allFoundProjects);
		schoolEntity.setAdditionalInformation(generateAdditionalInformationAndPersistIfNotExisting(schoolPostbody));
		schoolEntity.setShortSchoolName(schoolPostbody.getShortSchoolName());
		schoolEntity.setSchoolName(schoolPostbody.getSchoolName());
		schoolEntity.setMatchingCriterias(allMatchingSchoolCriterias);
		schoolEntity.setAddress(schoolPostbody.getAddress());
		fillSchoolType(schoolPostbody, schoolEntity);
		schoolEntity.setGeneralEmail(schoolPostbody.getGeneralEmail());
		schoolEntity.setGeneralPhoneNumber(schoolPostbody.getGeneralPhoneNumber());
		schoolEntity.setHomepage(schoolPostbody.getHomepage());
		if (schoolPostbody.getSchoolPicture() != null) {
			schoolEntity.setSchoolPicture(schoolPostbody.getSchoolPicture().getBytes());
		} else {
			schoolEntity.setSchoolPicture(null);
		}
		schoolEntity.setAlternativePictureText(schoolPostbody.getAlternativePictureText());
		schoolEntity.getPersonSchoolMapping().clear();
		schoolEntity.getProjects().clear();
		allFoundProjects.forEach(project -> {
			schoolEntity.getProjects().add(project);
		});
		fillPersonSchoolMappingOfSchool(schoolPostbody, schoolEntity);
	}

	private void fillSchoolType(SchoolDTO schoolPostbody, School schoolEntity) {
		if (schoolPostbody.getSchoolType() == null) {
			throw new BadArgumentsException("School Type not present, but neccessary!");
		}
		Optional<SchoolType> oneBySchoolTypeValue = schoolTypeRepo.findOneBySchoolTypeValue(
				SchoolTypeValue.toSchoolTypeValue(schoolPostbody.getSchoolType().getSchoolTypeValue()));
		if (oneBySchoolTypeValue.isEmpty()) {
			throw new BadArgumentsException("School Type not present, but neccessary!");
		}
		schoolEntity.setType(oneBySchoolTypeValue.get());
	}

	private void addPrimaryProjectToSchoolPostbody(SchoolDTO alterSchoolPostbody, School matchingSchool,
			List<Project> allFoundProjects) {
		if (alterSchoolPostbody.getPrimaryProject() == null) {
			throw new BadArgumentsException(alterSchoolPostbody);
		}
		List<Project> primaryProjectFromAllFoundProjects = allFoundProjects.stream()
				.filter(e -> e.getId() == Long.valueOf(alterSchoolPostbody.getPrimaryProject().getId()))
				.collect(Collectors.toList());
		if (primaryProjectFromAllFoundProjects.size() != 1) {
			throw new BadArgumentsException(alterSchoolPostbody);
		}
		matchingSchool.setPrimaryProject(primaryProjectFromAllFoundProjects.get(0));
	}

	@GetMapping("/search/findPersonFunctionalityForPersonAndSchoolAndFunctionality")
	public ResponseEntity<Long> getPersonFunctionalityIDForPersonAndSchoolAndFunctionality(long personId, long schoolId,
			String functionality) {
		Optional<School> school = schoolRepo.findById(schoolId);
		Optional<Person> person = personRepo.findById(personId);
		if (person.isEmpty()) {
			throw new ResourceNotFoundException(personId, "Person could not be found");
		} else if (school.isEmpty()) {
			throw new ResourceNotFoundException(schoolId, "School could not be found");
		}
		Optional<PersonSchoolMapping> personSchoolMapping = personSchoolMappingRepo
				.findOneBySchoolAndPersonAndFunctionality(school.get(), person.get(), functionality.toString());
		if (personSchoolMapping.isEmpty()) {
			throw new ResourceNotFoundException(person.toString(),
					"The person has no mapping to school " + school.toString());
		}
		return ResponseEntity.ok(personSchoolMapping.get().getId());
	}

	@Operation(summary = "deletes a school resource")
	@DeleteMapping("/delete/deleteSchool")
	public ResponseEntity<SchoolDTO> deleteSchool(long schoolId) {
		schoolRepo.deleteById(schoolId);
		finderEntries.clear();
		finderEntries.buildEntryList();
		return ResponseEntity.ok().build();
	}

	private void fillPersonSchoolMappingOfSchool(SchoolDTO alterSchoolPostbody, School matchingSchool) {

		alterSchoolPostbody.getPersonSchoolMapping().forEach(e -> {
			Optional<Person> personById = personRepo.findById(e.getPerson().getId());
			if (personById.isEmpty()) {
				throw new ResourceNotFoundException(alterSchoolPostbody, "person");
			}
			if (e.getFunctionality() == null) {
				throw new BadArgumentsException(e);
			}
			Optional<Functionality> matchingFunctionalityOptional = functionalityRepo
					.findOneByName(e.getFunctionality().getName());
			PersonSchoolMapping personSchoolMapping = new PersonSchoolMapping(personById.get(), matchingSchool,
					matchingFunctionalityOptional.get());
			personSchoolMapping.setDescription(e.getDescription());
			personSchoolMapping.setInstitutionalFunctionality(e.getInstitutionalFunctionality());
			matchingSchool.getPersonSchoolMapping().add(personSchoolMapping);
		});
	}

	private List<Project> generateProjectEntityListForSchoolPostbody(SchoolDTO addNewSchoolPostbody)
			throws NotFoundException {
		List<Project> allFoundProjects = new ArrayList<>();
		if (addNewSchoolPostbody.getProjects() == null) {
			return allFoundProjects;
		}
		for (ProjectDTO e : addNewSchoolPostbody.getProjects()) {
			Optional<Project> projectEntity = projectRepo.findById(Long.valueOf(e.getId()));
			if (projectEntity.isEmpty()) {
				throw new NotFoundException("One of the requested Projects could not be found!");
			}
			allFoundProjects.add(projectEntity.get());
		}
		return allFoundProjects;

	}

	/**
	 * Generates a list with all additional information (as entities, must be
	 * retrived within a transaction) and persists them if they are not present
	 * 
	 * @param postbody the school dto post
	 * @return a list with all additional information objects
	 */
	private List<AdditionalInformation> generateAdditionalInformationAndPersistIfNotExisting(SchoolDTO postbody) {
		List<AdditionalInformation> resultList = new ArrayList<>();
		if (postbody.getAdditionalInformation() == null) {
			return resultList;
		}
		postbody.getAdditionalInformation().forEach(e -> {
			Optional<InformationType> informationTypeOptional = informationTypeRepo.findOneByValue(e.getType());
			if (informationTypeOptional.isEmpty()) {
				throw new BadArgumentsException("One of the additional information types could not be found!");
			}
			Optional<AdditionalInformation> additionalInformationOptional;
			if (e.getHomepage() != null && !e.getHomepage().isBlank()) {
				additionalInformationOptional = additionalInformationRepo.findOneByValueAndTypeAndHomepage(e.getValue(),
						informationTypeOptional.get(), e.getHomepage());
			} else {
				additionalInformationOptional = additionalInformationRepo.findOneByValueAndType(e.getValue(),
						informationTypeOptional.get());
			}
			AdditionalInformation toAdd = null;
			if (additionalInformationOptional.isEmpty()) {
				toAdd = new AdditionalInformation();
				toAdd.setType(informationTypeOptional.get());
				toAdd.setValue(e.getValue());
				toAdd.setHomepage(e.getHomepage());
				toAdd = additionalInformationRepo.save(toAdd);
			} else {
				toAdd = additionalInformationOptional.get();
			}
			resultList.add(toAdd);
		});
		return resultList;
	}

	private List<Criteria> generateMatchingSchoolCriteriasAndPersistIfNotExisting(SchoolDTO alterSchoolPostbody) {
		List<Criteria> allMatchingSchoolCriterias = new ArrayList<>();
		if (alterSchoolPostbody.getMatchingCriterias() != null) {
			allMatchingSchoolCriterias = criteriaRepo.findAllByCriteriaNameIn(alterSchoolPostbody.getMatchingCriterias()
					.stream().map(Criteria::getCriteriaName).collect(Collectors.toList()));
			List<Criteria> listForStream = allMatchingSchoolCriterias;
			alterSchoolPostbody.getMatchingCriterias().forEach(e -> {
				if (!listForStream.stream().anyMatch(f -> f.getCriteriaName().equalsIgnoreCase(e.getCriteriaName()))) {
					Criteria savedCrit = criteriaRepo.save(new Criteria(e.getCriteriaName()));
					listForStream.add(savedCrit);
				}
			});
		}
		return allMatchingSchoolCriterias;
	}

	private boolean isSchoolPostbodyNotValid(SchoolDTO addNewSchoolPostbody) {
		return addNewSchoolPostbody.getPersonSchoolMapping() == null
				|| addNewSchoolPostbody.getPersonSchoolMapping().isEmpty();
	}
}
