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
package de.rathsolutions.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Type;
import org.springframework.transaction.annotation.Transactional;

import de.rathsolutions.controller.postbody.AdditionalInformationDTO;
import de.rathsolutions.controller.postbody.SchoolDTO;
import de.rathsolutions.jpa.entity.additional.AdditionalInformation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor

public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shortSchoolName;

    @NonNull
    private String schoolName;
    @NonNull
    private Double latitude;
    @NonNull
    private Double longitude;

    private byte[] schoolPicture;
    @Type(type = "text")
    private String alternativePictureText;
    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "school", orphanRemoval = true)
    private List<PersonSchoolMapping> personSchoolMapping = new ArrayList<>();
    @NonNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "school_criteria_mapping", joinColumns = @JoinColumn(name = "school_id"), inverseJoinColumns = @JoinColumn(name = "criteria_id"))
    private List<Criteria> matchingCriterias;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @ManyToOne(optional = false)
    private Project primaryProject;

    @ManyToOne()
    private SchoolType type;

    @ManyToMany
    @JoinTable(name = "additional_information_mapping", joinColumns = @JoinColumn(name = "school_id"), inverseJoinColumns = @JoinColumn(name = "additional_information_id"))
    private List<AdditionalInformation> additionalInformation = new ArrayList<>();

    private String generalEmail;

    private String generalPhoneNumber;

    private String address;

    private String homepage;

    public School(String shortSchoolName, String schoolName, Double latitude, Double longitude,
	    List<Criteria> matchingCriterias) {
	this.shortSchoolName = shortSchoolName;
	this.schoolName = schoolName;
	this.latitude = latitude;
	this.longitude = longitude;
	this.matchingCriterias = matchingCriterias;
    }

    public SchoolDTO convertToDTO() {
	SchoolDTO dto = new SchoolDTO();
	dto.setAlternativePictureText(this.alternativePictureText);
	dto.setId(this.id);
	dto.setLatitude(this.latitude);
	dto.setLongitude(this.longitude);
	dto.setMatchingCriterias(getMatchingCriterias());
	this.personSchoolMapping.forEach(e -> {
	    dto.getPersonSchoolMapping().add(e.convertToDTO());
	});
	this.projects.forEach(e -> {
	    dto.getProjects().add(e.convertToDto());
	});
	this.additionalInformation.forEach(e -> {
	    dto.getAdditionalInformation()
		    .add(new AdditionalInformationDTO(e.getId(), e.getValue(), e.getType().getValue()));
	});
	dto.setPrimaryProject(this.primaryProject.convertToDto());
	dto.setSchoolName(this.schoolName);
	if (this.schoolPicture != null) {
	    dto.setSchoolPicture(new String(this.schoolPicture));
	}
	dto.setShortSchoolName(this.shortSchoolName);
	dto.setAddress(this.address);
	dto.setHomepage(this.homepage);
	dto.setGeneralEmail(this.generalEmail);
	dto.setGeneralPhoneNumber(this.generalPhoneNumber);
	if (this.type != null) {
	    dto.setSchoolType(this.type.convertToDto());
	}
	return dto;
    }

    @Transactional
    public List<String> getSearchableInformation() {
	List<String> toReturn = new ArrayList<>();
	toReturn.add(this.address);
	toReturn.add(this.homepage);
	toReturn.add(this.generalEmail);
	toReturn.add(this.generalPhoneNumber);
	toReturn.add(this.schoolName);
	toReturn.add(this.shortSchoolName);
	this.additionalInformation.forEach(e -> toReturn.add(e.getValue()));
	toReturn.removeIf(e -> e == null);
	return toReturn;
    }

}
