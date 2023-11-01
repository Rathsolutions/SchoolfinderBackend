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
package de.rathsolutions.jpa.entity.additional;

import java.util.List;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.usertype.UserTypeLegacyBridge;

import de.rathsolutions.controller.postbody.AdditionalInformationDTO;
import de.rathsolutions.jpa.entity.School;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class AdditionalInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Type(value = UserTypeLegacyBridge.class, parameters = @Parameter(name = UserTypeLegacyBridge.TYPE_NAME_PARAM_KEY, value = "text"))
	private String value;

	private String homepage;

	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.PERSIST,
			CascadeType.REFRESH })
	private InformationType type;

	@ManyToMany(mappedBy = "additionalInformation")
	private List<School> matchingSchools;

	public AdditionalInformationDTO convertToDTO() {
		if (homepage == null || homepage.isBlank()) {
			return new AdditionalInformationDTO(id, value, type.getValue());
		}
		return new AdditionalInformationDTO(id, value, type.getValue(), homepage);

	}
}
