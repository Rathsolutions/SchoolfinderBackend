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
package de.rathsolutions.util.osm.specific;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Node;

import de.rathsolutions.util.osm.generic.AbstractOsmPOIParser;
import de.rathsolutions.util.osm.generic.OsmTags;
import de.rathsolutions.util.structure.OsmCityEntries;
import de.rathsolutions.util.structure.OsmEntries;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class OsmPOICityOnlyParser extends AbstractOsmPOIParser {

    @Autowired
    private OsmCityEntries osmCityEntries;
    
    @Override
    protected String getOsmFileName() {
        return "filteredCities.xml";
    }

    /**
     * Stub with nothing to do in this implementation
     */
    @Override
    protected void cleanup() {

    }

    /**
     * Stub with nothing to do in this implementation
     */
    @Override
    protected void init() {
    }

    @Override
    protected String getSecondInformationCriteriaAsString(Node currentNode) {
        String key = currentNode.getAttributes().getNamedItem("k").getTextContent();
        String val = currentNode.getAttributes().getNamedItem("v").getTextContent();
        if (OsmTags.IS_IN.getValue().equals(key)) {
            // Removing not needed information after second ','
            String[] splittedVal = val.split(",");
            if (splittedVal.length >= 2) {
                return splittedVal[0] + " - " + splittedVal[1];
            } else {
                return val;
            }
        } else if (OsmTags.WIKIPEDIA.getValue().equals(key)) {
            // Removing ':de'
            return val.substring(3);
        }
        return "";
    }

    @Override
    protected OsmEntries getCachedEntries() {
        return this.osmCityEntries;
    }
}
