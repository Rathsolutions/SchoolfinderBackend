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

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.controller.postbody.ProjectDTO;
import de.rathsolutions.jpa.entity.Project;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.ProjectRepo;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private ProjectRepo projectRepo;

    @GetMapping("/search/getProjectByName/{name}")
    public ResponseEntity<ProjectDTO> getProjectByName(@PathVariable(name = "name", required = true) String name) {
	Optional<Project> projectByName = projectRepo.findOneByProjectName(name);
	if (projectByName.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(projectByName.get().convertToDto());
    }

    @GetMapping("/search/getAllSchoolsForProjectWithName")
    public ResponseEntity<List<School>> getAllSchoolsForProjectWithName(
	    @PathVariable(name = "name", required = true) String name) {
	Optional<Project> projectByName = projectRepo.findOneByProjectName(name);
	if (projectByName.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(projectByName.get().getAllSchools());
    }

    @PutMapping("/create")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectToCreate) {
	System.out.println(projectToCreate.toString());
	Project projectEntity = new Project(projectToCreate.getName(), projectToCreate.getIcon());
	return ResponseEntity.ok(projectRepo.save(projectEntity).convertToDto());
    }

    @PatchMapping("/edit")
    @Transactional
    public ResponseEntity<ProjectDTO> editProject(@RequestBody ProjectDTO projectToCreate) {
	Optional<Project> projectEntityOptional = projectRepo.findById(Long.valueOf(projectToCreate.getId()));
	if (projectEntityOptional.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	Project projectEntity = projectEntityOptional.get();
	projectEntity.setProjectName(projectToCreate.getName());
	projectEntity.setDefaultIcon(projectToCreate.getIcon());
	return ResponseEntity.ok(projectRepo.save(projectEntity).convertToDto());
    }

}
