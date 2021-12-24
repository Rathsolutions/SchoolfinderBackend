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
package de.rathsolutions.util.finder.generic;

import java.util.Comparator;

import de.rathsolutions.util.finder.pojo.FinderEntity;

public class NearestNodesComparator implements Comparator<FinderEntity> {

    private static final double COMPARISON_DELTA = 0.01;
    private FinderEntity nodeToCompare;

    public NearestNodesComparator(FinderEntity n) {
	this.nodeToCompare = n;
    }

    @Override
    public int compare(FinderEntity o1, FinderEntity o2) {
	double firstDistance = HaversineUtils.calculateHaversine(nodeToCompare.getLatVal(), nodeToCompare.getLongVal(),
		o1.getLatVal(), o1.getLongVal());
	double secondDistance = HaversineUtils.calculateHaversine(nodeToCompare.getLatVal(), nodeToCompare.getLongVal(),
		o2.getLatVal(), o2.getLongVal());
	if (firstDistance - secondDistance == 0) {
	    return 0;
	}
	if (firstDistance - secondDistance > COMPARISON_DELTA) {
	    return 1;
	} else if (firstDistance - secondDistance < COMPARISON_DELTA) {
	    return -1;
	} else {
	    return 0;
	}
    }

}
