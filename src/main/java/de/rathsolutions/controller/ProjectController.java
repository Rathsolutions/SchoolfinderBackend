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
