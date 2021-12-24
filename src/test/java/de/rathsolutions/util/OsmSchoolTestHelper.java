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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.FinderEntitySearchConstraint;

public class OsmSchoolTestHelper {

    private static OsmSchoolTestHelper instance;

    private List<FinderEntity> testObjects;

    private OsmSchoolTestHelper() {
	testObjects = new ArrayList<>();
	testObjects.add(new FinderEntity("Berufliches Bildungszentrum", null,
		Arrays.asList(new FinderEntitySearchConstraint("Berufliches Bildungszentrum", "")), 48.9408981,
		8.3967966));
	testObjects.add(new FinderEntity("Max-Planck-Gymnasium", "Böblingen",
		Arrays.asList(new FinderEntitySearchConstraint("Max-Planck-Gymnasium", "Böblingen")), 48.6796386,
		9.0251714));
	testObjects.add(new FinderEntity("Grund- und Hauptschule Gemmrigheim", "Gemmrigheim",
		Arrays.asList(new FinderEntitySearchConstraint("Grund- und Hauptschule Gemmrigheim", "Gemmrigheim")),
		49.0286584, 9.1596199));
	testObjects
		.add(new FinderEntity("Franz-Oberthür-Schule Städtisches Berufsbildungszentrum I", null,
			Arrays.asList(new FinderEntitySearchConstraint(
				"Franz-Oberthür-Schule Städtisches Berufsbildungszentrum I", "")),
			49.7860555, 9.9543711));
	testObjects.add(new FinderEntity("Rheinauschule", "Mannheim",
		Arrays.asList(new FinderEntitySearchConstraint("Rheinauschule", "Mannheim")), 49.4328595, 8.5259034));
	testObjects.add(new FinderEntity("Grundschule Ipsheim", null,
		Arrays.asList(new FinderEntitySearchConstraint("Grundschule Ipsheim", null)), 49.5268451, 10.4792362));
	testObjects.add(new FinderEntity("Logements", null,
		Arrays.asList(new FinderEntitySearchConstraint("Logements", "")), 47.5788175, 7.5530249));
	testObjects.add(new FinderEntity("Spielwegschule", "Münstertal",
		Arrays.asList(new FinderEntitySearchConstraint("Spielwegschule", "Münstertal")), 47.8778643,
		7.8362427));
	testObjects.add(new FinderEntity("Neunlindenschule", "Ihringen",
		Arrays.asList(new FinderEntitySearchConstraint("Neunlindenschule", "Ihringen")), 48.0445650,
		7.6490466));
	testObjects.add(new FinderEntity("Rheinschulhaus", "Schaffhausen",
		Arrays.asList(new FinderEntitySearchConstraint("Rheinschulhaus", "Schaffhausen")), 47.6937530,
		8.6342400));
	testObjects.add(new FinderEntity("Schulhaus Obertor", null,
		Arrays.asList(new FinderEntitySearchConstraint("Schulhaus Obertor", "")), 47.4935356, 9.2434062));
	testObjects.add(new FinderEntity("Grundschule Illerbeuren", null,
		Arrays.asList(new FinderEntitySearchConstraint("Grundschule Illerbeuren", "")), 47.9023859,
		10.1276518));
	testObjects.add(new FinderEntity("Bildungszentrum Bretzfeld", null,
		Arrays.asList(new FinderEntitySearchConstraint("Bildungszentrum Bretzfeld", "")), 49.1787183,
		9.4359423));
	testObjects.add(new FinderEntity("Grundschule Waltershofen", "Kißlegg",
		Arrays.asList(new FinderEntitySearchConstraint("Grundschule Waltershofen", "Kißlegg")), 47.7569435,
		9.9184315));
	testObjects.add(new FinderEntity("Flattichschule", null,
		Arrays.asList(new FinderEntitySearchConstraint("Flattichschule", "")), 48.9368834, 9.2009058));
    }

    public List<FinderEntity> getTestEntites() {
	return Collections.unmodifiableList(testObjects);
    }

    public static OsmSchoolTestHelper getInstance() {
	if (instance == null) {
	    instance = new OsmSchoolTestHelper();
	}
	return instance;
    }

}
