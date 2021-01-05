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
package de.rathsolutions.util;

import lombok.Getter;

public enum OsmTags {

    NAME("name"), CITY("addr:city"), IS_IN("is_in"), WIKIPEDIA("wikipedia");

    @Getter
    private String value;

    private OsmTags(String value) {
        this.value = value;
    }

    public static boolean isValidTag(String tagName) {
        for (OsmTags e : OsmTags.values()) {
            if (e.getValue().equals(tagName)) {
                return true;
            }
        }
        return false;
    }
}
