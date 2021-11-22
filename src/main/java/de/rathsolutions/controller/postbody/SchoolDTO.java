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
package de.rathsolutions.controller.postbody;

import de.rathsolutions.jpa.entity.Criteria;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolDTO {

    private long id;

    private String shortSchoolName;

    private String schoolName;

    private double latitude;

    private double longitude;

    private String schoolPicture;

    private String alternativePictureText;

    private List<PersonFunctionalityDTO> personSchoolMapping = new ArrayList<>();

    private List<Criteria> matchingCriterias = new ArrayList<>();

    private List<ProjectDTO> projects = new ArrayList<>();

    private ProjectDTO primaryProject;

    private String correspondingAreaName;

    private List<AdditionalInformationDTO> additionalInformation = new ArrayList<>();

    private SchoolTypeDTO schoolType;

    private String address;

    private String generalPhoneNumber;

    private String generalEmail;

    private String homepage;

}
