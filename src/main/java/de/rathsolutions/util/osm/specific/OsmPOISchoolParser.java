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

import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Node;

import de.rathsolutions.util.osm.generic.AbstractOsmPOIParser;
import de.rathsolutions.util.osm.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.osm.generic.OsmTags;
import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.OsmPOIEntity;
import de.rathsolutions.util.structure.OsmEntries;
import de.rathsolutions.util.structure.OsmSchoolEntries;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)

@Slf4j
public class OsmPOISchoolParser extends AbstractOsmPOIParser {

    @Autowired
    private OsmSchoolEntries osmSchoolEntries;

    @Autowired
    private LevenstheinDistanceUtil levenstheinDistanceUtil;

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

    @Override
    protected List<OsmPOIEntity> generateResult(List<OsmPOIEntity> resultList,
            AbstractSearchEntity searchEntity, int amount) throws OperationNotSupportedException {
        if (resultList.isEmpty()) {
            return null;
        }
        String cityOrEmpty = searchEntity.getCity() != null ? searchEntity.getCity() : "";
        String fullName = searchEntity.getName() + cityOrEmpty;
        fullName = fullName.replaceAll("-", "").replaceAll("\\s+", "").toLowerCase();
        List<OsmPOIEntity> nearest = levenstheinDistanceUtil.computeLevenstheinDistance(fullName,
            resultList, amount, !cityOrEmpty.isEmpty());
        log.debug("Final entity: " + nearest.toString());
        return nearest;
    }

}
