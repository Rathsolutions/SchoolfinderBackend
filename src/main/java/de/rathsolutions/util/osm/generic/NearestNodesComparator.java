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
package de.rathsolutions.util.osm.generic;

import java.util.Comparator;

import de.rathsolutions.util.osm.pojo.OsmPOIEntity;

public class NearestNodesComparator implements Comparator<OsmPOIEntity> {

    private static final double COMPARISON_DELTA = 0.00001;
    private OsmPOIEntity nodeToCompare;

    public NearestNodesComparator(OsmPOIEntity n) {
        this.nodeToCompare = n;
    }

    @Override
    public int compare(OsmPOIEntity o1, OsmPOIEntity o2) {
        double firstDistance = HaversineUtils.calculateHaversine(nodeToCompare.getLatVal(),
            nodeToCompare.getLongVal(), o1.getLatVal(), o1.getLongVal());
        double secondDistance = HaversineUtils.calculateHaversine(nodeToCompare.getLatVal(),
            nodeToCompare.getLongVal(), o2.getLatVal(), o2.getLongVal());
        if (firstDistance - secondDistance > COMPARISON_DELTA) {
            return 1;
        } else if (firstDistance - secondDistance < COMPARISON_DELTA) {
            return -1;
        } else {
            return 0;
        }
    }

}
