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

public abstract class AbstractSearchEntity {

    protected String name;

    protected String city;

    protected String district;

    protected String street;

    protected String housenumber;

    /**
     * @return the name
     */
    public String getName() throws OperationNotSupportedException {
	return name;
    }

    /**
     * @return the city
     */
    public String getCity() throws OperationNotSupportedException {
	return city;
    }

    /**
     * @return the street
     */
    public String getStreet() throws OperationNotSupportedException {
	return street;
    }

    /**
     * @return the housenumber
     */
    public String getHousenumber() throws OperationNotSupportedException {
	return housenumber;
    }

    /**
     * @return the district
     */
    public String getDistrict() throws OperationNotSupportedException {
	return district;
    }

}
