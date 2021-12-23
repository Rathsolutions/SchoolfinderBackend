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
package de.rathsolutions.jpa.service;

import de.rathsolutions.jpa.entity.School;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SchoolDAOService {

    public List<String> getGeneralSearchableInformation(School entity) {
	List<String> toReturn = new ArrayList<>();
	toReturn.add(entity.getAddress());
	toReturn.add(entity.getHomepage());
	toReturn.add(entity.getGeneralEmail());
	toReturn.add(entity.getGeneralPhoneNumber());
	toReturn.add(entity.getSchoolName());
	toReturn.add(entity.getShortSchoolName());
	toReturn.removeIf(e -> e == null || e.isEmpty() || e.isBlank());
	return toReturn;
    }

    @Transactional
    public List<String> getAdditionalSearchableInformation(School entity) {
	List<String> toReturn = entity.getAdditionalInformation().stream().map(e -> e.getValue())
		.collect(Collectors.toList());
	toReturn.removeIf(e -> e == null || e.isEmpty() || e.isBlank());
	return toReturn;
    }
}
