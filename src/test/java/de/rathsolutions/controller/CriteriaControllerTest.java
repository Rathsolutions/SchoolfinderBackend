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
package de.rathsolutions.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.CriteriaController;
import de.rathsolutions.controller.postbody.AddNewCriteriaPostbody;
import de.rathsolutions.jpa.entity.Criteria;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Transactional
@Sql(scripts = "classpath:data-import.sql")
class CriteriaControllerTest {

    @Autowired
    private CriteriaController cut;

    @Test
    void testGetAllAvailableCriterias() throws Exception {
        ResponseEntity<List<Criteria>> allAvailableCriterias = cut.getAllAvailableCriterias();
        assertEquals(3, allAvailableCriterias.getBody().size());
        for (int i = 0; i < allAvailableCriterias.getBody().size(); i++) {
            String name = "test" + (i > 0 ? i : "");
            assertEquals(name, allAvailableCriterias.getBody().get(i).getCriteriaName());
        }
    }

    @Test
    void testGetCriteriaRecommendationsWithOnlyTName() throws Exception {
        ResponseEntity<List<Criteria>> criteriaRecommendations
                = cut.getCriteriaRecommendations("t", "10");
        assertEquals(3, criteriaRecommendations.getBody().size());
        for (int i = 0; i < criteriaRecommendations.getBody().size(); i++) {
            String name = "test" + (i > 0 ? i : "");
            assertEquals(name, criteriaRecommendations.getBody().get(i).getCriteriaName());
        }
    }

    @Test
    void testGetCriteriaRecommendationsWithFullQualifiedTestName() throws Exception {
        ResponseEntity<List<Criteria>> criteriaRecommendations
                = cut.getCriteriaRecommendations("test", "10");
        assertEquals(3, criteriaRecommendations.getBody().size());
        for (int i = 0; i < criteriaRecommendations.getBody().size(); i++) {
            String name = "test" + (i > 0 ? i : "");
            assertEquals(name, criteriaRecommendations.getBody().get(i).getCriteriaName());
        }
    }

    @Test
    void testGetCriteriaRecommendationsWithFullQualifiedTest1Name() throws Exception {
        ResponseEntity<List<Criteria>> criteriaRecommendations
                = cut.getCriteriaRecommendations("test1", "10");
        assertEquals(1, criteriaRecommendations.getBody().size());
        String name = "test1";
        assertEquals(name, criteriaRecommendations.getBody().get(0).getCriteriaName());
    }

    @Test
    void testGetCriteriaRecommendationsWithFullQualifiedTest2Name() throws Exception {
        ResponseEntity<List<Criteria>> criteriaRecommendations
                = cut.getCriteriaRecommendations("test2", "10");
        assertEquals(1, criteriaRecommendations.getBody().size());
        String name = "test2";
        assertEquals(name, criteriaRecommendations.getBody().get(0).getCriteriaName());
    }

    @Test
    void testAddNewCriteriaEmptyPostbody() throws Exception {
        ResponseEntity<Criteria> criteria = cut.addNewCriteria(null);
        assertEquals(HttpStatus.NO_CONTENT, criteria.getStatusCode());
        assertNull(criteria.getBody());
    }

    @Test
    void testAddNewCriteriaAlreadyExisting() throws Exception {
        ResponseEntity<Criteria> criteria = cut.addNewCriteria(new AddNewCriteriaPostbody("test"));
        assertEquals(HttpStatus.CONFLICT, criteria.getStatusCode());
        assertNull(criteria.getBody());
    }

    @Test
    void testAddNewCriteriaNewCriteria() throws Exception {
        ResponseEntity<Criteria> criteria = cut.addNewCriteria(new AddNewCriteriaPostbody("test3"));
        assertEquals(HttpStatus.OK, criteria.getStatusCode());
        assertEquals("test3", criteria.getBody().getCriteriaName());
        assertEquals(0, criteria.getBody().getSchoolMappings().size());
    }

}
