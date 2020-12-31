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

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.controller.postbody.AddNewCriteriaPostbody;
import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.repo.CriteriaRepo;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/criterias")
public class CriteriaController {
    @Autowired
    private CriteriaRepo criteriaRepo;

    @Operation(summary = "queries for all available filter criterias in the database")
    @GetMapping("/search/getAllAvailableCriterias")
    public ResponseEntity<List<Criteria>> getAllAvailableCriterias() {
        return ResponseEntity.ok(criteriaRepo.findAll());
    }

    @Operation(summary = "queries for criteria recommendations by a substring of an already existing criteria")
    @GetMapping("/search/getCriteriaRecommendations")
    public ResponseEntity<List<Criteria>> getCriteriaRecommendations(String criteria,
            String amount) {
        PageRequest page = PageRequest.of(0, Integer.valueOf(amount));
        return ResponseEntity.ok(criteriaRepo.findAllByCriteriaNameContaining(criteria, page));
    }

    @Operation(summary = "creates a new criteria resource in the database")
    @PutMapping("/create/addNewCriteria")
    public ResponseEntity<Criteria> addNewCriteria(AddNewCriteriaPostbody addNewCriteriaPostbody) {
        if (Objects.isNull(addNewCriteriaPostbody)) {
            return ResponseEntity.noContent().build();
        }
        if (criteriaRepo.existsByCriteriaName(addNewCriteriaPostbody.getCriteriaName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Criteria criteria = new Criteria(addNewCriteriaPostbody.getCriteriaName());
        return ResponseEntity.ok(criteriaRepo.save(criteria));
    }
}
