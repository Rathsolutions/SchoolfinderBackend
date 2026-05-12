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
package de.rathsolutions.util.finder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.jpa.repo.SchoolRepo;
import de.rathsolutions.util.structure.internalFinder.InstitutionAttributeFinderEntries;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Sql(scripts = "../../../../data-init.sql")
@Transactional
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class InstitutionAttributeFinderEntriesTest {

    @Autowired
    private InstitutionAttributeFinderEntries cut;

    @Autowired
    private SchoolRepo schoolRepo;

    @Test
    public void testGetMethodWithAllSchoolsHavingDateNull() {
        var finderEntryZero = cut.get(0);
        assertNotNull(finderEntryZero);
    }

    @Test
    public void testStreamMethodWithAllSchoolsHavingDateNull() {
        var finderEntryZero = cut.stream();
        assertNotNull(finderEntryZero);
    }

    @Test
    public void testSublistMethodWithAllSchoolsHavingDateNull() {
        var finderEntryZero = cut.sublistOfProjectId(-1);
        assertNotNull(finderEntryZero);
        assertEquals(9, finderEntryZero.size());
    }

    @Test
    public void testGetMethodWithOneSchoolHavingDateNonNull()
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        var attr = schoolRepo.findAll().get(0);
        Field lastCachedUpdateField = InstitutionAttributeFinderEntries.class.getDeclaredField("lastCachedUpdate");
        lastCachedUpdateField.setAccessible(true);
        assertNull(lastCachedUpdateField.get(cut));
        attr.setAddress("");
        schoolRepo.save(attr);
        var finderEntryZero = cut.get(0);
        Object currentLastUpdated = lastCachedUpdateField.get(cut);
        assertNotNull(currentLastUpdated);
        assertNotNull(finderEntryZero);
        cut.get(0);
        assertEquals(currentLastUpdated, lastCachedUpdateField.get(cut));

    }

    @Test
    public void testStreamMethodWithOneSchoolHavingDateNonNull()
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        var attr = schoolRepo.findAll().get(0);
        Field lastCachedUpdateField = InstitutionAttributeFinderEntries.class.getDeclaredField("lastCachedUpdate");
        lastCachedUpdateField.setAccessible(true);
        assertNull(lastCachedUpdateField.get(cut));
        attr.setAddress("");
        schoolRepo.save(attr);
        var finderEntryZero = cut.stream();
        Object currentLastUpdated = lastCachedUpdateField.get(cut);
        assertNotNull(currentLastUpdated);
        assertNotNull(finderEntryZero);
        cut.stream();
        assertEquals(currentLastUpdated, lastCachedUpdateField.get(cut));
    }

    @Test
    public void testSublistMethodWithOneSchoolHavingDateNonNull()
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        var attr = schoolRepo.findAll().get(0);
        Field lastCachedUpdateField = InstitutionAttributeFinderEntries.class.getDeclaredField("lastCachedUpdate");
        lastCachedUpdateField.setAccessible(true);
        assertNull(lastCachedUpdateField.get(cut));
        attr.setAddress("");
        schoolRepo.save(attr);
        var finderEntryZero = cut.sublistOfProjectId(-1);
        Object currentLastUpdated = lastCachedUpdateField.get(cut);
        assertNotNull(currentLastUpdated);
        assertNotNull(finderEntryZero);
        assertEquals(9, finderEntryZero.size());
        cut.sublistOfProjectId(-2);
        assertEquals(currentLastUpdated, lastCachedUpdateField.get(cut));
    }

}
