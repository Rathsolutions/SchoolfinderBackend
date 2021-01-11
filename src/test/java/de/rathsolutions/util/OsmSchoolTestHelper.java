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

import de.rathsolutions.util.osm.pojo.OsmPOIEntity;

public class OsmSchoolTestHelper {

    private static OsmSchoolTestHelper instance;

    private List<OsmPOIEntity> testObjects;

    private OsmSchoolTestHelper() {
        testObjects = new ArrayList<>();
        testObjects
                .add(new OsmPOIEntity("Berufliches Bildungszentrum", null, 48.9408981, 8.3967966));
        testObjects.add(new OsmPOIEntity("Max-Planck-Gymnasium", "Böblingen", 48.6796386, 9.0251714));
        testObjects.add(
            new OsmPOIEntity("Grund- und Hauptschule Gemmrigheim", "Gemmrigheim", 49.0286584, 9.1596199));
        testObjects
                .add(new OsmPOIEntity("Franz-Oberthür-Schule Städtisches Berufsbildungszentrum I",
                        null, 49.7860555, 9.9543711));
        testObjects.add(new OsmPOIEntity("Rheinauschule", "Mannheim", 49.4328595, 8.5259034));
        testObjects.add(new OsmPOIEntity("Grundschule Ipsheim", null, 49.5268451, 10.4792362));
        testObjects.add(new OsmPOIEntity("Logements", null, 47.5788175, 7.5530249));
        testObjects.add(new OsmPOIEntity("Spielwegschule", "Münstertal", 47.8778643, 7.8362427));
        testObjects.add(new OsmPOIEntity("Neunlindenschule", "Ihringen", 48.0445650, 7.6490466));
        testObjects.add(new OsmPOIEntity("Rheinschulhaus", "Schaffhausen", 47.6937530, 8.6342400));
        testObjects.add(new OsmPOIEntity("Schulhaus Obertor", null, 47.4935356, 9.2434062));
        testObjects.add(new OsmPOIEntity("Grundschule Illerbeuren", null, 47.9023859, 10.1276518));
        testObjects.add(new OsmPOIEntity("Bildungszentrum Bretzfeld", null, 49.1787183, 9.4359423));
        testObjects.add(new OsmPOIEntity("Grundschule Waltershofen", "Kißlegg", 47.7569435, 9.9184315));
        testObjects.add(new OsmPOIEntity("Flattichschule", null, 48.9368834, 9.2009058));
    }

    public List<OsmPOIEntity> getTestEntites() {
        return Collections.unmodifiableList(testObjects);
    }

    public static OsmSchoolTestHelper getInstance() {
        if (instance == null) {
            instance = new OsmSchoolTestHelper();
        }
        return instance;
    }

}
