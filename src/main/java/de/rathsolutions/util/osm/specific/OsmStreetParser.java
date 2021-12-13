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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.generic.DocumentParser;
import de.rathsolutions.util.osm.generic.HaversineUtils;
import de.rathsolutions.util.osm.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.osm.generic.NearestNodesComparator;
import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.osm.pojo.OsmStreetPojo;
import de.rathsolutions.util.structure.osm.OsmCityEntries;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Slf4j
public class OsmStreetParser extends OsmPOICityOnlyParser {
    @Autowired
    private DocumentParser documentParser;

    @Autowired
    private LevenstheinDistanceUtil levenstheinDistanceUtil;

    @Autowired
    private OsmCityEntries osmCityEntries;

    private List<OsmStreetPojo> allWayPojos;

    private File streetObjects;

    public void createStreetObjectsHeapFile() {
	Document document;
	try {
	    streetObjects = new File("src/main/resources/streetObjects.smaps");
	    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(streetObjects))) {
		document = documentParser.readDocument("filtered.xml");
		NodeList listOfAllWays = document.getElementsByTagName("way");
		allWayPojos = convertWaysToPojos(listOfAllWays);
		streetObjects.createNewFile();
		out.writeInt(allWayPojos.size());
		allWayPojos.forEach(e -> {
		    try {
			out.writeObject(e);
		    } catch (IOException e1) {
			e1.printStackTrace();
		    }

		});
		out.flush();
	    } catch (IOException e2) {
		log.error(e2.getMessage());
	    }
	} catch (ParserConfigurationException | SAXException e) {
	    log.error(e.getMessage());
	}
    }

    @Override
    protected List<FinderEntity> generateResult(List<FinderEntity> resultList, AbstractSearchEntity searchEntity,
	    int amount) throws OperationNotSupportedException {
	return findStreetGeocodes(searchEntity.getCity(), searchEntity.getStreet(), searchEntity.getHousenumber(),
		amount);
    }

    private List<FinderEntity> findStreetGeocodes(String city, String streetname, String houseNumber, int amount) {
	init();
	List<FinderEntity> nearestEntityToUserCityInput = levenstheinDistanceUtil.computeLevenstheinDistance(city,
		osmCityEntries, 3, false, true);
	List<FinderEntity> entitiesToTraverse = new ArrayList<>();
	List<FinderEntity> waysWithoutMappedCity = new ArrayList<>();
	try (ObjectInputStream in = new ObjectInputStream(
		new BufferedInputStream(new ClassPathResource("streetObjects.smaps").getInputStream()))) {
	    int size = in.readInt();
	    for (int j = 0; j < nearestEntityToUserCityInput.size(); j++) {
		waysWithoutMappedCity.clear();
		for (int i = 0; i < size; i++) {
		    OsmStreetPojo current = (OsmStreetPojo) in.readObject();
		    if (current.getCity().isEmpty() || current.getCity() == null) {
			waysWithoutMappedCity.add(new FinderEntity(current.getStreet(), current.getHousenumber(),
				current.getLatitude(), current.getLongitude()));
		    } else if (nearestEntityToUserCityInput.get(j).getPrimaryValue().equalsIgnoreCase(current.getCity())
			    || nearestEntityToUserCityInput.get(j).getPrimaryValue()
				    .equalsIgnoreCase(current.getSuburb())) {
			entitiesToTraverse.add(new FinderEntity(current.getStreet(), current.getHousenumber(),
				current.getLatitude(), current.getLongitude()));
		    }
		}
		Collections.sort(waysWithoutMappedCity,
			new NearestNodesComparator(nearestEntityToUserCityInput.get(j)));
		int i = 0;
		for (i = 0; i < waysWithoutMappedCity.size()
			&& HaversineUtils.calculateHaversine(nearestEntityToUserCityInput.get(j).getLatVal(),
				nearestEntityToUserCityInput.get(j).getLongVal(),
				waysWithoutMappedCity.get(i).getLatVal(),
				waysWithoutMappedCity.get(i).getLongVal()) < 5000; i++) {
		    entitiesToTraverse.add(waysWithoutMappedCity.get(i));
		}
	    }
	} catch (IOException | ClassNotFoundException e1) {
	    e1.printStackTrace();
	}
	return levenstheinDistanceUtil.computeLevenstheinDistance(streetname + (houseNumber != null ? houseNumber : ""),
		entitiesToTraverse, amount, houseNumber != null, true);
    }

    private List<OsmStreetPojo> convertWaysToPojos(NodeList listOfAllWays) {
	List<OsmStreetPojo> returnList = new ArrayList<>();
	for (int i = 0; i < listOfAllWays.getLength(); i++) {
	    Node currentItem = listOfAllWays.item(i);
	    NodeList childNodes = currentItem.getChildNodes();
	    String currentCity = null;
	    String currentStreet = null;
	    String currentHousenumber = null;
	    String currentSuburb = null;
	    double lat = Double.parseDouble(currentItem.getAttributes().getNamedItem("lat").getTextContent());
	    double lon = Double.parseDouble(currentItem.getAttributes().getNamedItem("lon").getTextContent());
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
		case "addr:suburb":
		    currentSuburb = textContent;
		default:
		    break;
		}
	    }
	    returnList.add(new OsmStreetPojo(currentCity, currentStreet, currentHousenumber, currentSuburb, lat, lon));
	}
	return returnList;
    }

}
