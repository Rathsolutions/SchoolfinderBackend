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
package de.rathsolutions.util.osm.pojo;

import javax.naming.OperationNotSupportedException;

public class CitySearchEntity extends AbstractSearchEntity {

    private static final String A_CITY_SEARCH_ENTITY_CANNOT_HAVE_A
            = "A CitySearchEntity cannot have a ";

    public CitySearchEntity(String city, String district) {
        this.city = city;
        this.district = district;
    }
    public CitySearchEntity(String city) {
        this.city = city;
        this.district = null;
    }
    @Override
    public String getHousenumber() throws OperationNotSupportedException {
        throw new OperationNotSupportedException(
                A_CITY_SEARCH_ENTITY_CANNOT_HAVE_A + "housenumber!");
    }

    @Override
    public String getName() throws OperationNotSupportedException {
        throw new OperationNotSupportedException(A_CITY_SEARCH_ENTITY_CANNOT_HAVE_A + "name!");
    }

    @Override
    public String getStreet() throws OperationNotSupportedException {
        throw new OperationNotSupportedException(A_CITY_SEARCH_ENTITY_CANNOT_HAVE_A + "street!");
    }
}
