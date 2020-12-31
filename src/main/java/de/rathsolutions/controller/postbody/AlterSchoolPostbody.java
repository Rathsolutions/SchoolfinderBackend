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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlterSchoolPostbody extends AddNewSchoolPostbody {
    private long id;

    public AlterSchoolPostbody(long id, String schoolName, double latitude, double longitude,
            String schoolPicture, String arContent,
            List<PersonFunctionalityEntity> personSchoolMapping, List<Criteria> criteriaNames) {
        super(schoolName, latitude, longitude, schoolPicture, arContent, personSchoolMapping,
                criteriaNames);
        this.id = id;
    }

}
