package de.rathsolutions.controller;

import de.rathsolutions.controller.postbody.AddNewDistrictPostbody;
import de.rathsolutions.jpa.entity.District;
import de.rathsolutions.jpa.repo.DistrictRepo;
import java.util.Optional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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

@RestController
@RequestMapping("/api/v1/district")
public class DistrictController {

    @Autowired
    private DistrictRepo districtRepo;

    @GetMapping("/search/getDistrictByName/{name}")
    public ResponseEntity<District> getDistrictByName(@PathVariable(name = "name", required = true) String name) {
	Optional<District> districtByName = districtRepo.findOneByDistrictName(name);
	if (districtByName.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(districtByName.get());
    }

    @PutMapping("/create/createDistrict")
    public ResponseEntity<District> createDistrict(@RequestBody AddNewDistrictPostbody postbody) {
	District district = new District();
	district.setDistrictName(postbody.getName());
	GeometryFactory geometryFactory = new GeometryFactory();
	Coordinate[] coordinates = new Coordinate[postbody.getPointList().size()];
	for (int i = 0; i < postbody.getPointList().size(); i++) {
	    coordinates[i] = postbody.getPointList().get(i);
	}
	district.setDistrictArea(geometryFactory.createPolygon(coordinates));
	return ResponseEntity.ok(districtRepo.save(district));
    }

    @PatchMapping("/edit/editDistrict")
    @Transactional
    public ResponseEntity<District> editDistrict(@RequestBody AddNewDistrictPostbody postbody) {
	Optional<District> districtOptional = districtRepo.findById(postbody.getId());
	if (districtOptional.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	District district = districtOptional.get();
	district.setDistrictName(postbody.getName());
	GeometryFactory geometryFactory = new GeometryFactory();
	Coordinate[] coordinates = new Coordinate[postbody.getPointList().size()];
	for (int i = 0; i < postbody.getPointList().size(); i++) {
	    coordinates[i] = postbody.getPointList().get(i);
	}
	district.setDistrictArea(geometryFactory.createPolygon(coordinates));
	return ResponseEntity.ok(districtRepo.save(district));
    }
}
