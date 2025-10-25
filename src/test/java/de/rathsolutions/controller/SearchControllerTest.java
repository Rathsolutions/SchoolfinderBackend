/*-
 * #%L
 * SchoolfinderBackend
 * %%
 * Copyright (C) 2020 - 2025 Rathsolutions. <info@rathsolutions.de>
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
package de.rathsolutions.controller;

import static org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import de.rathsolutions.SpringBootMain;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Sql(scripts = "../../../data-init.sql")
@Transactional
public class SearchControllerTest {

    @Autowired
    private SearchController searchController;

    @Test
    public void searchForValuesInAllProjects() {
        var searchEntries = searchController.findGeneralInstitutionContentInDatabase("additional_info", 10, null);
        var sortedResult = searchEntries.getBody().stream().sorted().collect(Collectors.toList());
        
        assertEquals(4, sortedResult.size());

        assertEquals("testschool", sortedResult.get(0).getPrimaryValue());
        assertEquals("testschool" + 2, sortedResult.get(1).getPrimaryValue());
        assertEquals("testschool" + 3, sortedResult.get(2).getPrimaryValue());
        assertEquals("testschool" + 4, sortedResult.get(3).getPrimaryValue());

        assertEquals("additional_info" + 1, sortedResult.get(0).getSecondaryValue());
        assertEquals("additional_info" + 2, sortedResult.get(1).getSecondaryValue());
        assertEquals("additional_info" + 3, sortedResult.get(2).getSecondaryValue());

    }

    @Test
    public void searchForValuesInOneProjects() {
        var searchEntries = searchController.findGeneralInstitutionContentInDatabase("additional_info", 10, -2L);
        var sortedResult = searchEntries.getBody().stream().sorted().collect(Collectors.toList());
        
        assertEquals(2, sortedResult.size());

        assertEquals("testschool" + 3, sortedResult.get(0).getPrimaryValue());
        assertEquals("testschool" + 4, sortedResult.get(1).getPrimaryValue());

        assertEquals("additional_info" + 3, sortedResult.get(0).getSecondaryValue());

    }
}
