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

import org.hibernate.annotations.Type;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

    private String color;
    @Lob
    private String arContent;
    @Lob
    private String makerspaceContent;
    @Lob
    private String schoolPicture;
    @Lob
    private String alternativePictureText;
    @OneToMany(cascade = {
            CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "school", orphanRemoval = true)
    private List<PersonSchoolMapping> personSchoolMapping = new ArrayList<>();
    @NonNull
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "school_criteria_mapping", joinColumns = @JoinColumn(name = "school_id"), inverseJoinColumns = @JoinColumn(name = "criteria_id"))
    private List<Criteria> matchingCriterias;

    public School(String shortSchoolName, String schoolName, Double latitude, Double longitude, List<Criteria> matchingCriterias) {
	this.shortSchoolName = shortSchoolName;
	this.schoolName = schoolName;
	this.latitude = latitude;
	this.longitude = longitude;
	this.matchingCriterias = matchingCriterias;
    }
    
}
