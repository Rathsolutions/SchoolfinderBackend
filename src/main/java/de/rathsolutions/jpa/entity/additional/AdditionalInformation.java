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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import de.rathsolutions.controller.postbody.AdditionalInformationDTO;
import de.rathsolutions.jpa.entity.School;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter

public class AdditionalInformation {

    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String value;

    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.PERSIST,
	    CascadeType.REFRESH })
    private InformationType type;

    @ManyToMany(mappedBy = "additionalInformation")
    private List<School> matchingSchools;

    public AdditionalInformationDTO convertToDTO() {
	return new AdditionalInformationDTO(id, value, type.getValue());
    }
}
