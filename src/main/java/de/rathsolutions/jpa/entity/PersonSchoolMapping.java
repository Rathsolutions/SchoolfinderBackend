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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.usertype.UserTypeLegacyBridge;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.rathsolutions.controller.postbody.PersonFunctionalityDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
	@ManyToOne()
	private Functionality functionality;

	@Type(value = UserTypeLegacyBridge.class, parameters = @Parameter(name = UserTypeLegacyBridge.TYPE_NAME_PARAM_KEY, value = "text"))
	private String description;

	private String institutionalFunctionality;

	public PersonFunctionalityDTO convertToDTO() {
		PersonFunctionalityDTO personFunctionalityDTO = new PersonFunctionalityDTO();
		personFunctionalityDTO.setFunctionality(this.functionality);
		personFunctionalityDTO.setMappingId(this.id);
		personFunctionalityDTO.setPerson(this.person);
		personFunctionalityDTO.setDescription(this.description);
		personFunctionalityDTO.setInstitutionalFunctionality(institutionalFunctionality);
		return personFunctionalityDTO;
	}

}
