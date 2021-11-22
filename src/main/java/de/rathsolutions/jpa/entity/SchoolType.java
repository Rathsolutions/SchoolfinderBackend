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
package de.rathsolutions.jpa.entity;

import de.rathsolutions.controller.postbody.SchoolTypeDTO;
import de.rathsolutions.jpa.entity.converter.ColorConverter;
import java.awt.Color;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SchoolType {

    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private SchoolTypeValue schoolTypeValue;

    @Convert(converter = ColorConverter.class)
    private Color color;

    @OneToMany(mappedBy = "type")
    private List<School> allSchools;

    public SchoolTypeDTO convertToDto() {
	SchoolTypeDTO dto = new SchoolTypeDTO();
	dto.setB(color.getBlue());
	dto.setG(color.getGreen());
	dto.setR(color.getRed());
	dto.setSchoolTypeValue(schoolTypeValue.getValue());
	return dto;
    }
}
