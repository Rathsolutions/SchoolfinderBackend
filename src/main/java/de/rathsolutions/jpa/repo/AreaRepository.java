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
package de.rathsolutions.jpa.repo;

import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.Area;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

	public Optional<Area> findOneByNameIgnoreCase(String name);

	@Query(value = "SELECT * FROM public.area WHERE ST_contains(area, ST_Transform(:p, 3857))", nativeQuery = true)
	public List<Area> findAreasContainingPoint(Point p);

//	@Query(value = "select ST_ConcaveHull(ST_Collect(a.area),0.3) from area as a", nativeQuery = true)
//	public Optional<Polygon> findConcaveHull();
}
