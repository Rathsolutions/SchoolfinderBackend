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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.util.osm.pojo.OsmPOIEntity;
import de.rathsolutions.util.osm.specific.OsmStreetParser;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Slf4j
class OsmStreetParserTest {

    @Autowired
    private OsmStreetParser cut;

    @Test
    void testCityStreetSearch() {
        List<OsmPOIEntity> findStreetGeocodes
                = cut.findStreetGeocodes("Rastatt", "Engelstraße", "21", 1);
        findStreetGeocodes.stream().forEach(e -> {
            assertEquals(48.859834600000006, e.getLatVal());
            assertEquals(8.201058, e.getLongVal());
            System.out.println(e.getPrimaryValue());
            System.out.println(e.getSecondaryValue());
        });
    }

    //Use this test to create the requried heap file for the city street search
    @Disabled
    @Test
    void writeHeapFile() {
        cut.createStreetObjectsHeapFile();
    }

}
