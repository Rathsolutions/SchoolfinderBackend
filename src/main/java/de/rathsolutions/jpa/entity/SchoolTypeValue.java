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

import lombok.Getter;

public enum SchoolTypeValue {

    GYMNASIUM("Gymnasium"), REALSCHULE("Realschule"), BERUFLICHE_SCHULE("Berufliche Schule"),
    WALDORFSCHULE("Waldorfschule"), GRUNDSCHULE("Grundschule"), SBBZ("SBBZ"),
    GEMEINSCHAFTSSCHULE("Gemeinschaftsschule"), WERKREALSCHULE("(Werk)realschule");

    @Getter
    private String value;

    private SchoolTypeValue(String value) {
	this.value = value;
    }

    public static SchoolTypeValue toSchoolTypeValue(String rep) {
	for (SchoolTypeValue s : values()) {
	    if (s.getValue().equalsIgnoreCase(rep)) {
		return s;
	    }
	}
	return null;
    }
}
