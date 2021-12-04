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

import de.rathsolutions.controller.postbody.SchoolTypeDTO;
import de.rathsolutions.jpa.entity.SchoolType;
import de.rathsolutions.jpa.entity.SchoolTypeValue;
import de.rathsolutions.jpa.repo.SchoolTypeRepo;
import io.swagger.v3.oas.annotations.Operation;
import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PatchMapping("/edit")
    public ResponseEntity<SchoolType> edit(@RequestBody SchoolTypeDTO dto) {
	Optional<SchoolType> existingEntity = schoolTypeRepo
		.findOneBySchoolTypeValue(SchoolTypeValue.toSchoolTypeValue(dto.getSchoolTypeValue()));
	if (existingEntity.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	SchoolType type = existingEntity.get();
	type.setColor(new Color(dto.getR(), dto.getG(), dto.getB()));
	type.setSchoolTypeValue(SchoolTypeValue.toSchoolTypeValue(dto.getSchoolTypeValue()));
	return ResponseEntity.ok(schoolTypeRepo.save(type));
    }

    @Operation(summary = "retrieves all known school types")
    @GetMapping("/search/findAll")
    public ResponseEntity<List<SchoolTypeDTO>> getAllTypes() {
	return ResponseEntity
		.ok(schoolTypeRepo.findAll().stream().map(e -> e.convertToDto()).collect(Collectors.toList()));
    }
}
