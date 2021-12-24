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
package de.rathsolutions.util.finder.pojo;

import javax.naming.OperationNotSupportedException;

public class SchoolSearchEntity extends AbstractSearchEntity {

    private static final String A_SCHOOL_SEARCH_ENTITY_CANNOT_HAVE_A = "A SchoolSearchEntity cannot have a ";

    public SchoolSearchEntity(String name, String city) {
	this.name = name;
	this.city = city;
    }

    @Override
    public String getHousenumber() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_SCHOOL_SEARCH_ENTITY_CANNOT_HAVE_A + " housenumber!");
    }

    @Override
    public String getStreet() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_SCHOOL_SEARCH_ENTITY_CANNOT_HAVE_A + " street!");
    }

    @Override
    public String getDistrict() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_SCHOOL_SEARCH_ENTITY_CANNOT_HAVE_A + " district!");
    }
}
