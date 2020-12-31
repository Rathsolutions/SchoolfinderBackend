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

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;

import de.rathsolutions.jpa.entity.OsmPOIEntity;

@Slf4j
public abstract class AbstractOsmPOIParser extends AbstractOsmPOIHandler {

    @Autowired
    private LevenstheinDistanceUtil levenstheinDistanceUtil;

    @Override
    protected List<OsmPOIEntity> generateResult(List<OsmPOIEntity> resultList) {
        if (resultList.isEmpty()) {
            return null;
        }
        String fullName = this.queryValue + (this.city != null ? this.city : "");
        fullName = fullName.replaceAll("-", "").replaceAll("\\s+", "").toLowerCase();
        List<OsmPOIEntity> nearest
                = levenstheinDistanceUtil.computeLevenstheinDistance(fullName, resultList, amount);
        log.debug("Final entity: " + nearest.toString());
        return nearest;
    }

    protected boolean valMatchCriteria(String val, String match) {
        val = val.replaceAll("-", "").replaceAll("\\s+", "").toLowerCase();
        match = match.replaceAll("-", "").replaceAll("\\s+", "").toLowerCase();
        return val.equalsIgnoreCase(match) || val.trim().equalsIgnoreCase(match.trim())
                || val.contains(match) || match.contains(val);
    }

    /**
     * Handles the current found entry.
     */
    @Override
    protected OsmPOIEntity handleKeyFound(Element nodeItem, Element nameTag, Element cityTag,
            Element overallItem) {
        if (nameTag == null) {
            return null;
        }
        String name = nameTag.getAttributes().getNamedItem("v").getTextContent();
        String city = getCityForKeyFound(nameTag, cityTag);
        return new OsmPOIEntity(name, city, Double.valueOf(nodeItem.getAttribute("lat")),
                Double.valueOf(nodeItem.getAttribute("lon")));
    }

    protected abstract String getCityForKeyFound(Element nameTag, Element cityTag);

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
}
