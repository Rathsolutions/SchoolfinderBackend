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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.rathsolutions.jpa.entity.OsmPOIEntity;

@Service
@Scope("prototype")
public class OsmPOIReducer extends AbstractOsmPOIHandler {

    private BufferedWriter writer;
    private Transformer transformer;

    @Override
    protected OsmPOIEntity handleKeyFound(Element nodeItem, Element nameTag, Element overallItem) {
        if (nameTag == null) {
            return null;
        }
        String name = nameTag.getAttributes().getNamedItem("v").getTextContent();
        String city = name;
        NodeList childNodes = overallItem.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node tagNode = childNodes.item(i);
            if (tagNode != null && tagNode.getAttributes() != null && !OsmTags
                    .isValidTag(tagNode.getAttributes().getNamedItem("k").getTextContent())) {
                overallItem.removeChild(tagNode);
            }
        }
        DOMSource source = new DOMSource(overallItem);
        StreamResult result = new StreamResult(new StringWriter());
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        String strObject = result.getWriter().toString();
        try {
            writer.write(strObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new OsmPOIEntity(name, city, Double.valueOf(nodeItem.getAttribute("lat")),
                Double.valueOf(nodeItem.getAttribute("lon")));
    }

    @Override
    protected void cleanup() {
        try {
            this.writer.write("</osm>");
            this.writer.flush();
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        try {
            File file = new File("filteredCitiesNew.xml");
            file.createNewFile();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            this.writer = new BufferedWriter(new FileWriter(file));
            this.writer.write("<osm>");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Not needed in this implementation
     */
    @Override
    protected List<OsmPOIEntity> generateResult(List<OsmPOIEntity> resultList) {
        return null;
    }

    @Override
    protected String getOsmFileName() {
        return "bawu_cities.xml";
    }

}
