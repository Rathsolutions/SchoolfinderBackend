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

import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

public final class GeometryUtils {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private GeometryUtils() {

    }

    public static Point createPoint(double latitude, double longitude) {
	return GEOMETRY_FACTORY.createPoint(new Coordinate(latitude, longitude));
    }

    public static Polygon createPolygon(List<Coordinate> coordinateList) {
	Polygon polygon = GEOMETRY_FACTORY.createPolygon(coordinateList.toArray(new Coordinate[coordinateList.size()]));
	polygon.setSRID(3857);
	return polygon;
    }
}
