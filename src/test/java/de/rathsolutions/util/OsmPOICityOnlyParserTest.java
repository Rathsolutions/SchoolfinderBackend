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

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.jpa.entity.OsmPOIEntity;
import de.rathsolutions.util.osm.specific.OsmPOICityOnlyParser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javassist.NotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.xml.sax.SAXException;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Slf4j
class OsmPOICityOnlyParserTest {

    @Autowired
    private OsmPOICityOnlyParser cut;

    @Test
    void testFindCorrectCitiesWithFullName()
            throws ParserConfigurationException, SAXException, IOException, NotFoundException,
            TransformerException, InterruptedException, ExecutionException {
        List<OsmPOIEntity> testObjects = OsmCityTestHelper.getInstance().getTestEntites();
        for (OsmPOIEntity e : testObjects) {
            List<OsmPOIEntity> schoolByName
                    = cut.processOsmFile(e.getPrimaryValue(), e.getSecondaryValue(), 1);
            OsmTestHelper.assertOsmPoiEqual(e, schoolByName.get(0));
        }
    }

    @Test
    void testFindOnPerfectMatchMoreThanOneElement()
            throws ParserConfigurationException, SAXException, IOException, NotFoundException,
            TransformerException, InterruptedException, ExecutionException {
        List<OsmPOIEntity> testObjects = new ArrayList<>();
        String city = "Steinbach";
        testObjects.add(new OsmPOIEntity(city, city, 48.7288702, 8.1607982));
        testObjects.add(new OsmPOIEntity(city, city, 48.9575768, 9.4738062));
        for (OsmPOIEntity e : testObjects) {
            List<OsmPOIEntity> schoolByName = cut.processOsmFile(e.getPrimaryValue(), 10);
            long exactElementCount = schoolByName.stream()
                    .filter(f -> f.getPrimaryValue().equals(e.getPrimaryValue())
                            && e.getLatVal() == f.getLatVal() && e.getLongVal() == f.getLongVal())
                    .count();
            assertEquals(1, exactElementCount);
        }
    }

}
