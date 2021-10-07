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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.controller.postbody.AreaDTO;
import de.rathsolutions.jpa.entity.Area;
import de.rathsolutions.jpa.repo.AreaRepository;
import de.rathsolutions.util.GeometryUtils;

@RestController
@RequestMapping("/api/v1/area/")
public class AreaController {

    @Autowired
    private AreaRepository repository;

    @GetMapping("/search/findByName")
    public ResponseEntity<AreaDTO> findByName(@RequestParam(value = "name") String name) {
	Optional<Area> areaOptional = repository.findOneByNameIgnoreCase(name);
	if (areaOptional.isEmpty()) {
	    return ResponseEntity.badRequest().build();
	}
	return ResponseEntity.ok(areaOptional.get().convertToDTO());
    }

    @GetMapping("/search/findAll")
    public ResponseEntity<List<AreaDTO>> findAll() {
	return ResponseEntity.ok(repository.findAll().stream().map(e -> e.convertToDTO()).collect(Collectors.toList()));
    }

    @PutMapping(value = "/create")
    public ResponseEntity<AreaDTO> create(@RequestBody AreaDTO dto) {
	Optional<Area> areaByName = repository.findOneByNameIgnoreCase(dto.getName());
	if (areaByName.isPresent()) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	Area area = new Area();
	fillArea(dto, area);
	return ResponseEntity.ok(repository.save(area).convertToDTO());
    }

    @PatchMapping(value = "/edit")
    public ResponseEntity<AreaDTO> edit(@RequestBody AreaDTO dto) {
	Optional<Area> areaByName = repository.findOneByNameIgnoreCase(dto.getName());
	if (areaByName.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	Area area = areaByName.get();
	fillArea(dto, area);
	return ResponseEntity.ok(repository.save(area).convertToDTO());
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Long> delete(@PathVariable(name = "id") long id) {
	if (!repository.existsById(id)) {
	    return ResponseEntity.notFound().build();
	}
	repository.deleteById(id);
	return ResponseEntity.ok(id);
    }

    private Area fillArea(AreaDTO dto, Area area) {
	area.setName(dto.getName());
	area.setColor(dto.getColor());
	org.locationtech.jts.geom.Point locationPoint = GeometryUtils.createPoint(
		dto.getAreaInstitutionPosition().getLatitude(), dto.getAreaInstitutionPosition().getLongitude());
	area.setAreaInstitutionPosition(locationPoint);
	List<Coordinate> coordinates = new ArrayList<>();
	dto.getAreaPolygon().forEach(e -> {
	    coordinates.add(new Coordinate(e.getLatitude(), e.getLongitude()));
	});
	area.setArea(GeometryUtils.createPolygon(coordinates));
	return area;
    }

}
