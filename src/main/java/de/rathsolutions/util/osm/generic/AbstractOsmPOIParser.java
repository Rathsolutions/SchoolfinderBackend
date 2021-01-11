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
package de.rathsolutions.util.osm.generic;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.pojo.OsmPOIEntity;

@Slf4j
public abstract class AbstractOsmPOIParser extends AbstractOsmPOIHandler {

    @Autowired
    private LevenstheinDistanceUtil levenstheinDistanceUtil;

    @Override
    protected List<OsmPOIEntity> generateResult(List<OsmPOIEntity> resultList, String primaryValue,
            String secondaryValue, int amount) {
        if (resultList.isEmpty()) {
            return null;
        }
        String cityOrEmpty = secondaryValue != null ? secondaryValue : "";
        String fullName = primaryValue + cityOrEmpty;
        fullName = fullName.replaceAll("-", "").replaceAll("\\s+", "").toLowerCase();
        List<OsmPOIEntity> nearest = levenstheinDistanceUtil.computeLevenstheinDistance(fullName,
            resultList, amount, secondaryValue != null && !secondaryValue.isEmpty());
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
    protected OsmPOIEntity handleKeyFound(Element nodeItem, Element nameTag, Element overallItem) {
        if (nameTag == null) {
            return null;
        }
        String name = nameTag.getAttributes().getNamedItem("v").getTextContent();
        String city = getSecondInformationForEntity(overallItem);
        return new OsmPOIEntity(name, city, Double.valueOf(nodeItem.getAttribute("lat")),
                Double.valueOf(nodeItem.getAttribute("lon")));
    }

    protected abstract String getSecondInformationCriteriaAsString(Node currentNode);

    private String getSecondInformationForEntity(Element currentNodeElement) {
        NodeList childNodes = currentNodeElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node tagNode = childNodes.item(i);
            if (tagNode != null && tagNode.getAttributes() != null
                    && tagNode.getAttributes().getNamedItem("k") != null) {
                String ret = getSecondInformationCriteriaAsString(tagNode);
                if (!ret.isBlank()) {
                    return ret;
                }
            }
        }
        return "";
    }

    /**
     * Stub with nothing to do in this implementation
     */
    @Override
    protected void cleanup() {

    }

    @Override
    protected void init() {
        try {
            if (getCachedEntries().isEmpty()) {
                buildNodeCache();
            }
        } catch (ParserConfigurationException | SAXException | IOException | InterruptedException
                | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
