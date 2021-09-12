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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.controller.postbody.FunctionalityDTO;
import de.rathsolutions.jpa.entity.Functionality;
import de.rathsolutions.jpa.repo.FunctionalityRepo;

@RestController
@RequestMapping("/api/v1/functionality")
public class FunctionalityController {

    @Autowired
    private FunctionalityRepo functionalityRepo;

    @GetMapping(value = "/search/findAll")
    public ResponseEntity<List<Functionality>> findAll() {
	return ResponseEntity.ok(functionalityRepo.findAll());
    }

    @GetMapping(value = "/search/findByName")
    public ResponseEntity<Functionality> findByName(@RequestParam String name) {
	Optional<Functionality> functionalityOptional = functionalityRepo.findOneByName(name);
	if (functionalityOptional.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(functionalityOptional.get());
    }

    @PutMapping(value = "/create")
    public ResponseEntity<FunctionalityDTO> create(@RequestBody FunctionalityDTO entity) {
	Functionality functionalityToCreate = new Functionality();
	if (functionalityRepo.existsByName(entity.getName())) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	functionalityToCreate.setName(entity.getName());
	return ResponseEntity.ok(functionalityRepo.save(functionalityToCreate).convertToDto());
    }

    @PatchMapping(value = "/edit")
    @Transactional
    public ResponseEntity<FunctionalityDTO> edit(@RequestBody FunctionalityDTO entity) {
	Optional<Functionality> functionalityOptional = functionalityRepo.findById(Long.valueOf(entity.getId()));
	if (functionalityOptional.isEmpty()) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	functionalityOptional.get().setName(entity.getName());
	return ResponseEntity.ok(functionalityRepo.save(functionalityOptional.get()).convertToDto());
    }
}
