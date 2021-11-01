package de.rathsolutions.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.rathsolutions.jpa.repo.AdditionalInformationRepo;
import de.rathsolutions.jpa.repo.InformationTypeRepo;

@RestController
@RequestMapping("/api/v1/additional-information/")
public class AdditionalInformationController {

    @Autowired
    private AdditionalInformationRepo additionalInformationRepo;

    @Autowired
    private InformationTypeRepo informationTypeRepo;

}
