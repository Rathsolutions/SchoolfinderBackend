/*-
 * #%L
 * SchoolfinderBackend
 * %%
 * Copyright (C) 2020 - 2021 Rathsolutions. <info@rathsolutions.de>
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
import static org.junit.Assert.assertNull;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.postbody.ProjectDTO;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.ProjectRepo;
import java.util.List;
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
@Sql(scripts = "../../../data.sql")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ProjectControllerTest {

    @Autowired
    private ProjectController cut;

    @Autowired
    private ProjectRepo projectRepo;

    @Test
    public void testFindById() {
	assertProjects(cut.findById(-1).getBody(), projectRepo.findById(-1L).get().convertToDto());

    }

    @Test
    public void testFindAll() {
	ResponseEntity<List<ProjectDTO>> findByName = cut.findAll();
	findByName.getBody().forEach(e -> {
	    assertProjects(projectRepo.findById(e.getId()).get().convertToDto(), e);
	});
    }

    @Test
    public void testGetProjectByName() {
	ResponseEntity<ProjectDTO> projectByName = cut.getProjectByName("testproj1");
	assertProjects(projectRepo.findById(-1L).get().convertToDto(), projectByName.getBody());
    }

    @Test
    public void testGetAllSchoolsForProjectWithName() {
	List<School> matchingSchools = cut.getAllSchoolsForProjectWithName("testproj1").getBody();
	assertEquals(2, matchingSchools.size());
	assertEquals(-1L, matchingSchools.get(0).getId().longValue());
	assertEquals(-2L, matchingSchools.get(1).getId().longValue());
    }

    @Test
    public void testGetAllSchoolsForProjectWithId() {
	List<School> matchingSchools = cut.getAllSchoolsForProjectWithName("testproj2").getBody();
	assertEquals(1, matchingSchools.size());
	assertEquals(-3L, matchingSchools.get(0).getId().longValue());
    }

    @Test
    public void testCreateProject() {
	ProjectDTO projectToCreate = new ProjectDTO();
	projectToCreate.setId(1);
	projectToCreate.setName("testproj3");
	projectToCreate.setScaling(2.0);
	projectToCreate.setIcon("testicon");
	ResponseEntity<ProjectDTO> responseEntity = cut.createProject(projectToCreate);
	assertProjects(projectToCreate, responseEntity.getBody());
    }

    @Test
    public void testCreateProjectAlreadyExistingName() {
	ProjectDTO projectToCreate = new ProjectDTO();
	projectToCreate.setId(1);
	projectToCreate.setName("testproj1");
	projectToCreate.setScaling(2.0);
	projectToCreate.setIcon("testicon");
	ResponseEntity<ProjectDTO> responseEntity = cut.createProject(projectToCreate);
	assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
	assertNull(responseEntity.getBody());
    }

    @Test
    public void testEditProject() {
	ProjectDTO projectToCreate = new ProjectDTO();
	projectToCreate.setId(-1);
	projectToCreate.setName("testproj1");
	projectToCreate.setScaling(2.0);
	projectToCreate.setIcon("testicon");
	ResponseEntity<ProjectDTO> responseEntity = cut.editProject(projectToCreate);
	assertProjects(projectToCreate, responseEntity.getBody());
    }

    @Test
    public void testEditProjectNotExistingId() {
	ProjectDTO projectToCreate = new ProjectDTO();
	projectToCreate.setId(1);
	projectToCreate.setName("testproj3");
	projectToCreate.setScaling(2.0);
	projectToCreate.setIcon("testicon");
	ResponseEntity<ProjectDTO> responseEntity = cut.editProject(projectToCreate);
	assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	assertNull(responseEntity.getBody());
    }

    private void assertProjects(ProjectDTO expected, ProjectDTO actual) {
	assertEquals(expected.getId(), actual.getId());
	assertEquals(expected.getIcon(), actual.getIcon());
	assertEquals(expected.getName(), actual.getName());
	assertEquals(expected.getScaling(), actual.getScaling());
    }
}
