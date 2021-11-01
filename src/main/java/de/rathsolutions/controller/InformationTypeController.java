package de.rathsolutions.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import de.rathsolutions.controller.postbody.InformationTypeDTO;
import de.rathsolutions.jpa.entity.additional.InformationType;
import de.rathsolutions.jpa.repo.InformationTypeRepo;

@RestController
@RequestMapping("/api/v1/informationType/")
public class InformationTypeController {

    @Autowired
    private InformationTypeRepo informationTypeRepo;

    @GetMapping("/search/findAll")
    public ResponseEntity<List<InformationTypeDTO>> findAllTypes() {
	List<InformationTypeDTO> result = informationTypeRepo.findAll().stream()
		.map(e -> new InformationTypeDTO(e.getId(), e.getValue())).collect(Collectors.toList());
	if (result.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/search/findByName")
    public ResponseEntity<InformationTypeDTO> findByName(@RequestParam String name) {
	Optional<InformationType> functionalityOptional = informationTypeRepo.findOneByValue(name);
	if (functionalityOptional.isEmpty()) {
	    return ResponseEntity.notFound().build();
	}
	return ResponseEntity.ok(functionalityOptional.get().convertToDTO());
    }

    @PutMapping(value = "/create")
    public ResponseEntity<InformationTypeDTO> create(@RequestBody InformationTypeDTO entity) {
	InformationType functionalityToCreate = new InformationType();
	if (informationTypeRepo.existsByValue(entity.getName())) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	functionalityToCreate.setValue(entity.getName());
	return ResponseEntity.ok(informationTypeRepo.save(functionalityToCreate).convertToDTO());
    }

    @PatchMapping(value = "/edit")
    @Transactional
    public ResponseEntity<InformationTypeDTO> edit(@RequestBody InformationTypeDTO entity) {
	Optional<InformationType> functionalityOptional = informationTypeRepo.findById(entity.getId());
	if (functionalityOptional.isEmpty()) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	Optional<InformationType> correspondingInDb = informationTypeRepo.findOneByValue(entity.getName());
	if (!correspondingInDb.isEmpty() && entity.getId() != correspondingInDb.get().getId()
		&& correspondingInDb.get().getValue().equalsIgnoreCase(entity.getName())) {
	    return ResponseEntity.status(HttpStatus.CONFLICT).build();
	}
	functionalityOptional.get().setValue(entity.getName());
	return ResponseEntity.ok(informationTypeRepo.save(functionalityOptional.get()).convertToDTO());
    }
}
