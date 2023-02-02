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
package de.rathsolutions.util;

import de.rathsolutions.util.finder.pojo.FinderEntity;

public class OsmTestHelper {
	public static boolean assertOsmPoiEqual(FinderEntity e, FinderEntity schoolByName) {
		System.out.println(schoolByName.getPrimaryValue());
		System.out.println(e.getLatVal() + "/" + e.getLongVal());
		return assertEquals(e.getLatVal(), schoolByName.getLatVal())
				&& assertEquals(e.getLongVal(), schoolByName.getLongVal())
				&& assertEquals(e.getPrimaryValue(), schoolByName.getPrimaryValue());
	}

	private static boolean assertEquals(Object first, Object scnd) {
		return first.equals(scnd);
	}
}
