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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.webjars.NotFoundException;
import org.xml.sax.SAXException;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.SchoolSearchEntity;
import de.rathsolutions.util.finder.specific.osm.OsmPOISchoolParser;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Slf4j
class OsmPOISchoolParserTest {

	@Autowired
	private OsmPOISchoolParser cut;

	@Test
	void testFindCorrectElementsInXmlFileWithFullName()
			throws ParserConfigurationException, SAXException, IOException, NotFoundException, TransformerException,
			InterruptedException, ExecutionException, OperationNotSupportedException {
		List<FinderEntity> testObjects = OsmSchoolTestHelper.getInstance().getTestEntites();
		for (FinderEntity e : testObjects) {
			List<FinderEntity> schoolByName = cut
					.find(new SchoolSearchEntity(e.getPrimaryValue(), e.getSecondaryValue()), 1);
			assertTrue(OsmTestHelper.assertOsmPoiEqual(e, schoolByName.get(0)));
		}
	}

}
