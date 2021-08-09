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

import de.rathsolutions.jpa.entity.Project;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.repo.ProjectRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

    @Autowired
    private ProjectRepo projectRepo;

    @GetMapping("/search/getProjectByName")
    public ResponseEntity<Project> getProjectByName(@PathVariable(name = "name", required = true) String name) {
	Optional<Project> projectByName = projectRepo.findOneByProjectName(name);
	if (projectByName.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(projectByName.get());
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

}
