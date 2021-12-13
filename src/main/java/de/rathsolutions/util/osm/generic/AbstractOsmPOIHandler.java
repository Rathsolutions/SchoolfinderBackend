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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.osm.specific.FinderService;
import javassist.NotFoundException;

public abstract class AbstractOsmPOIHandler implements FinderService {

    private static final String QUERYNAME_MUST_NOT_BE_NULL = "Queryname must not be null!";

    @Autowired
    private DocumentParser documentParser;

    @Override
    public List<FinderEntity> find(AbstractSearchEntity primaryValue, int amount)
	    throws OperationNotSupportedException, ParserConfigurationException, SAXException, IOException,
	    NotFoundException, TransformerException, InterruptedException, ExecutionException {
	if (primaryValue == null) {
	    throw new IllegalArgumentException(QUERYNAME_MUST_NOT_BE_NULL);
	}
	return processOsmFileInternal(primaryValue, amount);
    }

    private List<FinderEntity> processOsmFileInternal(AbstractSearchEntity searchEntity, int amount)
	    throws ParserConfigurationException, SAXException, IOException, NotFoundException, TransformerException,
	    InterruptedException, ExecutionException, OperationNotSupportedException {
	init();
	List<FinderEntity> resultList;
	if (getCachedEntries() == null || getCachedEntries().isEmpty()) {
	    resultList = buildWayCache();
	} else {
	    resultList = getCachedEntries();
	}
	List<FinderEntity> osmPoiInNodes = generateResult(resultList, searchEntity, amount);

	if (!Objects.isNull(osmPoiInNodes)) {
	    return osmPoiInNodes;
	}
	throw new NotFoundException("The element is not present!");
    }

    protected abstract List<FinderEntity> generateResult(List<FinderEntity> resultList,
	    AbstractSearchEntity searchEntity, int amount) throws OperationNotSupportedException;

    public List<FinderEntity> buildWayCache()
	    throws ParserConfigurationException, SAXException, IOException, InterruptedException, ExecutionException {
	List<FinderEntity> resultList;
	Document parsedXml = documentParser.readDocument(getOsmFileName());
	NodeList listOfAllNodes = parsedXml.getElementsByTagName("node");
	CompletableFuture<List<FinderEntity>> resultNodeList = traverseXmlFileByNodeList(listOfAllNodes);
	parsedXml = documentParser.readDocument(getOsmFileName());
	listOfAllNodes = parsedXml.getElementsByTagName("way");
	CompletableFuture<List<FinderEntity>> resultWayList = traverseXmlFileByNodeList(listOfAllNodes);
	cleanup();
	resultList = resultNodeList.get();
	resultList.addAll(resultWayList.get());
	getCachedEntries().addAll(resultList);
	return resultList;
    }

    protected abstract String getOsmFileName();

    @Async
    private CompletableFuture<List<FinderEntity>> traverseXmlFileByNodeList(NodeList nodeList) {
	Stream<Node> nodeStream = IntStream.range(0, nodeList.getLength()).mapToObj(nodeList::item);
	List<FinderEntity> entitiesToCheck = new ArrayList<>();
	nodeStream.forEach(e -> {
	    Element nodeItem = (Element) e;
	    NodeList childNodes = nodeItem.getChildNodes();
	    Element centerTag = null;
	    Element nameTag = null;
	    for (int j = 0; j < childNodes.getLength(); j++) {
		Node tagNode = childNodes.item(j);
		if (tagNode == null) {
		    continue;
		}
		NamedNodeMap attributes = tagNode.getAttributes();
		if (attributes == null) {
		    continue;
		}
		switch (tagNode.getNodeName()) {
		case "center":
		    centerTag = (Element) tagNode;
		    break;
		case "tag":
		    if ("name".equals(tagNode.getAttributes().getNamedItem("k").getTextContent())) {
			nameTag = (Element) tagNode;
		    }
		    break;
		default:
		    nodeItem.removeChild(tagNode);
		    break;
		}
	    }
	    FinderEntity foundEntity = handleKeyFound(centerTag != null ? centerTag : nodeItem, nameTag, nodeItem);
	    if (foundEntity != null) {
		entitiesToCheck.add(foundEntity);
	    }
	});
	return CompletableFuture.completedFuture(entitiesToCheck);
    }

    protected abstract void init();

    protected abstract FinderEntity handleKeyFound(Element nodeItem, Element tagNode, Element overallNodeItem);

    protected abstract void cleanup();

    protected abstract List<FinderEntity> getCachedEntries();

}
