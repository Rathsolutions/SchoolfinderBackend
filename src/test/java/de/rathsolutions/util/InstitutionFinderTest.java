/*-
 * #%L
 * SchoolfinderBackend
 * %%
 * Copyright (C) 2020 - 2023 Rathsolutions. <info@rathsolutions.de>
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.webjars.NotFoundException;
import org.xml.sax.SAXException;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.InstitutionSearchEntity;
import de.rathsolutions.util.finder.specific.InstitutionFinder;
import de.rathsolutions.util.structure.internalFinder.InstitutionAttributeFinderEntries;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
class InstitutionFinderTest {

	@Autowired
	private InstitutionFinder institutionFinder;
	
	@MockBean
	private InstitutionAttributeFinderEntries finderEntriesMock;
	
	
	@Test
	void test() {
		try {
			institutionFinder.find(new InstitutionSearchEntity(""), 10);
		} catch (NotFoundException | OperationNotSupportedException | ParserConfigurationException | SAXException
				| IOException | TransformerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}
