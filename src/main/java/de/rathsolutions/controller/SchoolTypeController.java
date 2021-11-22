package de.rathsolutions.controller;

import de.rathsolutions.controller.postbody.SchoolTypeDTO;
import de.rathsolutions.jpa.entity.SchoolType;
import de.rathsolutions.jpa.entity.SchoolTypeValue;
import de.rathsolutions.jpa.repo.SchoolTypeRepo;
import io.swagger.v3.oas.annotations.Operation;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/v1/schoolType")
public class SchoolTypeController {

    @Autowired
    private SchoolTypeRepo schoolTypeRepo;

    @PutMapping("/create")
    public ResponseEntity<SchoolType> create(@RequestBody SchoolTypeDTO dto) {
	SchoolType type = new SchoolType();
	type.setColor(new Color(dto.getR(), dto.getG(), dto.getB()));
	type.setSchoolTypeValue(SchoolTypeValue.toSchoolTypeValue(dto.getSchoolTypeValue()));
	return ResponseEntity.ok(schoolTypeRepo.save(type));

    }

    @Operation(summary = "retrieves all known school types")
    @GetMapping("/search/getAllTypes")
    public ResponseEntity<List<String>> getAllTypes() {
	List<String> toReturn = new ArrayList<>();
	for (SchoolTypeValue type : SchoolTypeValue.values()) {
	    toReturn.add(type.getValue());
	}
	return ResponseEntity.ok(toReturn);
    }
}
