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
package de.rathsolutions.util.finder.specific;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.xml.sax.SAXException;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.util.exception.ResourceNotFoundException;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.finder.pojo.FinderEntitySearchConstraint;
import de.rathsolutions.util.finder.pojo.InstitutionSearchEntity;
import de.rathsolutions.util.structure.internalFinder.InstitutionAttributeFinderEntries;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
class InstitutionFinderTest {

	@Autowired
	private InstitutionFinder institutionFinder;

	@MockitoBean
	private InstitutionAttributeFinderEntries finderEntriesMock;

	private List<FinderEntity> finderEntitiesForGeneralSearchList;

	private List<FinderEntity> finderEntitiesForProjectSpecificSearchList;

	private EasyRandom easyRandom = new EasyRandom();

	@BeforeEach
	void before() {
		finderEntitiesForGeneralSearchList = generateRandomFinderEntities(10);
		finderEntitiesForProjectSpecificSearchList = generateRandomFinderEntities(10);
		when(finderEntriesMock.stream()).thenReturn(finderEntitiesForGeneralSearchList.stream());
		when(finderEntriesMock.sublistOfProjectId(anyLong())).thenReturn(finderEntitiesForProjectSpecificSearchList);

	}

	private List<FinderEntity> generateRandomFinderEntities(int amount) {
		List<FinderEntity> toReturn = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			var schoolId = easyRandom.nextInt();
			var projectMakingId = easyRandom.nextInt();
			String secondaryValue = "School" + schoolId + " macht Projekt" + projectMakingId + " Sachen";
			toReturn.add(new FinderEntity("School" + schoolId, secondaryValue,
					Stream.of(secondaryValue.split(" ")).map(el -> new FinderEntitySearchConstraint(el, ""))
							.collect(Collectors.toList()),
					easyRandom.nextLong(),
					easyRandom.nextLong()));
		}
		return toReturn;
	}

	@ParameterizedTest
	@MethodSource("testRange")
	void findEntriesForAllProjects(int id) {
		try {
			var val = institutionFinder
					.find(new InstitutionSearchEntity(finderEntitiesForGeneralSearchList.get(id).getPrimaryValue()), 1);
			assertEquals(val.size(), 1);
			assertEquals(finderEntitiesForGeneralSearchList.get(id), val.get(0));
		} catch (ResourceNotFoundException | OperationNotSupportedException | ParserConfigurationException
				| SAXException
				| IOException | TransformerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@ParameterizedTest
	@MethodSource("testRange")
	void findEntriesForSpecificProject(int id) {
		try {
			var val = institutionFinder
					.find(new InstitutionSearchEntity(
							finderEntitiesForProjectSpecificSearchList.get(id).getPrimaryValue(), -2),
							1);
			assertEquals(val.size(), 1);
			assertEquals(finderEntitiesForProjectSpecificSearchList.get(id), val.get(0));
		} catch (ResourceNotFoundException | OperationNotSupportedException | ParserConfigurationException
				| SAXException
				| IOException | TransformerException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	private static IntStream testRange() {
		return IntStream.range(0, 10);
	}
}
