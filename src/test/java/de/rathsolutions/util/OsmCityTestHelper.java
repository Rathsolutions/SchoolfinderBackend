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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.rathsolutions.util.osm.pojo.FinderEntity;

public class OsmCityTestHelper {

    private static OsmCityTestHelper instance;

    private List<FinderEntity> testObjects;

    private OsmCityTestHelper() {
        testObjects = new ArrayList<>();
        testObjects.add(new FinderEntity("Ettlingen", null, 48.9397149, 8.40427));
        testObjects.add(new FinderEntity("Oberdischingen", null, 48.3025238, 9.8329152));
        testObjects.add(new FinderEntity("Oberndorf am Neckar", null, 48.2908613, 8.5711222));
        testObjects.add(new FinderEntity("Langenenslingen", null, 48.1480101, 9.3770185));
        testObjects.add(new FinderEntity("Baienfurt", null, 47.8289442, 9.6534108));
        testObjects.add(new FinderEntity("Iggingen", null, 48.8327373, 9.8772074));
        testObjects.add(new FinderEntity("Böbingen an der Rems", null, 48.8222298, 9.9178237));
        testObjects.add(new FinderEntity("Schonach im Schwarzwald", null, 48.1427148, 8.1974937));
        testObjects.add(new FinderEntity("Pfaffenweiler", "Villingen-Schwenningen", 48.0336388, 8.4195787));
        testObjects.add(new FinderEntity("Oberriexingen", null, 48.9274282, 9.0292153));
        testObjects.add(new FinderEntity("Triberg", null, 48.133228, 8.2331937));
        testObjects.add(new FinderEntity("Walheim", null, 49.0126104, 9.1519563));
        testObjects.add(new FinderEntity("Gerlingen", null, 48.7983947, 9.0624386));
        testObjects.add(new FinderEntity("Dielheim", null, 49.2824981, 8.7351709));
        testObjects.add(new FinderEntity("Brombach", null, 47.6347472, 7.6929302));
    }

    public List<FinderEntity> getTestEntites() {
        return Collections.unmodifiableList(testObjects);
    }

    public static OsmCityTestHelper getInstance() {
        if (instance == null) {
            instance = new OsmCityTestHelper();
        }
        return instance;
    }

}
