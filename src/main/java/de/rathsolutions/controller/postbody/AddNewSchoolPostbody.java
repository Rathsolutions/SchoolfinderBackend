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

import java.util.List;

import de.rathsolutions.jpa.entity.Criteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewSchoolPostbody {

    protected String shortSchoolName;
    
    protected String schoolName;

    protected double latitude;

    protected double longitude;
    
    protected String color;

    protected String schoolPicture;
    
    protected String alternativePictureText;
    
    protected String arContent;
    
    protected String makerspaceContent;

    protected List<PersonFunctionalityEntity> personSchoolMapping;

    protected List<Criteria> matchingCriterias;
}
