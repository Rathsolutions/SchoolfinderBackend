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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.rathsolutions.controller.postbody.PersonFunctionalityDTO;
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

public class PersonSchoolMapping {

//    @NonNull
//    @EmbeddedId
//    private PersonSchoolMappingKey id = new PersonSchoolMappingKey();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne
    private Person person;
    @NonNull
    @ManyToOne
    @JsonIgnore
    private School school;
    @NonNull
    @ManyToOne
    private Functionality functionality;

    @Type(type = "text")
    private String description;

    public PersonFunctionalityDTO convertToDTO() {
	PersonFunctionalityDTO personFunctionalityDTO = new PersonFunctionalityDTO();
	personFunctionalityDTO.setFunctionality(this.functionality);
	personFunctionalityDTO.setMappingId(this.id);
	personFunctionalityDTO.setPerson(this.person);
	personFunctionalityDTO.setDescription(this.description);
	return personFunctionalityDTO;
    }

}
