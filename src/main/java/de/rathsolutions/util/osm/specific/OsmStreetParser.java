/*-
 * #%L
 * SchoolfinderBackend
 * %%
 * Copyright (C) 2020 - 2021 Rathsolutions. <info@rathsolutions.de>
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.rathsolutions.jpa.entity.OsmPOIEntity;
import de.rathsolutions.util.osm.generic.DocumentParser;
import de.rathsolutions.util.osm.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.osm.pojo.OsmStreetPojo;

@Service
public class OsmStreetParser {
    @Autowired
    private DocumentParser documentParser;

    @Autowired
    private LevenstheinDistanceUtil levenstheinDistanceUtil;

    private List<OsmStreetPojo> allWayPojos;

    private File streetObjects;

    public void createStreetObjectsHeapFile() {
        Document document;
        try {
            streetObjects = new File("src/main/resources/streetObjects.smaps");
            document = documentParser.readDocument("filtered.xml");
            NodeList listOfAllWays = document.getElementsByTagName("way");
            allWayPojos = convertWaysToPojos(listOfAllWays);
            streetObjects.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(streetObjects));
            out.writeInt(allWayPojos.size());
            allWayPojos.forEach(e -> {
                try {
                    out.writeObject(e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            });
            out.flush();
            out.close();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public List<OsmPOIEntity> findStreetGeocodes(String city, String streetname, String houseNumber,
            int amount) {
        List<OsmPOIEntity> entitiesToTraverse = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(
                new ClassPathResource("streetObjects.smaps").getInputStream()))) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                OsmStreetPojo current = (OsmStreetPojo) in.readObject();
                if (current.getCity().equalsIgnoreCase(city)) {
                    entitiesToTraverse
                            .add(new OsmPOIEntity(current.getStreet(), current.getHousenumber(),
                                    current.getLatitude(), current.getLongitude()));
                }
            }
        } catch (IOException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return levenstheinDistanceUtil.computeLevenstheinDistance(
            streetname + (houseNumber != null ? houseNumber : ""), entitiesToTraverse, amount,
            houseNumber != null);
    }

    private List<OsmStreetPojo> convertWaysToPojos(NodeList listOfAllWays) {
        List<OsmStreetPojo> returnList = new ArrayList<>();
        for (int i = 0; i < listOfAllWays.getLength(); i++) {
            Node currentItem = listOfAllWays.item(i);
            NodeList childNodes = currentItem.getChildNodes();
            String currentCity = null;
            String currentStreet = null;
            String currentHousenumber = null;
            double lat = Double
                    .parseDouble(currentItem.getAttributes().getNamedItem("lat").getTextContent());
            double lon = Double
                    .parseDouble(currentItem.getAttributes().getNamedItem("lon").getTextContent());
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node currentChild = childNodes.item(j);
                NamedNodeMap attributes = currentChild.getAttributes();
                String textContent = attributes.getNamedItem("v").getTextContent();
                switch (attributes.getNamedItem("k").getTextContent()) {
                    case "addr:city":
                        currentCity = textContent;
                        break;
                    case "addr:street":
                        currentStreet = textContent;
                        break;
                    case "addr:housenumber":
                        currentHousenumber = textContent;
                        break;
                    default:
                        break;
                }
            }
            returnList.add(
                new OsmStreetPojo(currentCity, currentStreet, currentHousenumber, lat, lon));
        }
        return returnList;
    }
}
