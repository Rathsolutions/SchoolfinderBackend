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

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.rathsolutions.jpa.entity.OsmPOIEntity;
import de.rathsolutions.util.structure.OsmEntries;
import de.rathsolutions.util.structure.OsmSchoolEntries;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class OsmPOISchoolParser extends AbstractOsmPOIParser {

    @Autowired
    private OsmSchoolEntries osmSchoolEntries;

    @Override
    protected String getOsmFileName() {
        return "filteredSchools.xml";
    }

    protected String getSecondInformationCriteriaAsString(Node currentNode) {
        return OsmTags.CITY.getValue()
                .equals(currentNode.getAttributes().getNamedItem("k").getTextContent())
                        ? currentNode.getAttributes().getNamedItem("v").getTextContent()
                        : "";
    }

    @Override
    protected OsmEntries getCachedEntries() {
        return osmSchoolEntries;
    }
}
