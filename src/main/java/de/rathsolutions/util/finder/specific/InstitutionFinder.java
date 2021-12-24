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
package de.rathsolutions.util.finder.specific;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import de.rathsolutions.util.finder.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.finder.pojo.AbstractSearchEntity;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import de.rathsolutions.util.structure.internalFinder.InstitutionAttributeFinderEntries;
import javassist.NotFoundException;

@Service
public class InstitutionFinder implements FinderService {

    @Autowired
    private InstitutionAttributeFinderEntries cachedEntries;

    @Autowired
    private LevenstheinDistanceUtil distanceUtil;

    /**
     * Idea: Use a Trie Data structure for every single word and match all the
     * different words with levenstheins distance.
     */
    @Override
    public List<FinderEntity> find(AbstractSearchEntity primaryValue, int amount)
	    throws OperationNotSupportedException, ParserConfigurationException, SAXException, IOException,
	    NotFoundException, TransformerException, InterruptedException, ExecutionException {
	String queryValue = primaryValue.getName();
	String[] splittedOnSpace = queryValue.split(" ");
	if (splittedOnSpace.length == 1) {
	    return distanceUtil.computeLevenstheinDistance(queryValue, cachedEntries, amount, false, false);
	} else {
	    Map<FinderEntity, Integer> cumulatedElements = new HashMap<>();
	    for (int i = 0; i < splittedOnSpace.length; i++) {
		List<FinderEntity> resultList = distanceUtil.computeLevenstheinDistance(splittedOnSpace[i],
			cachedEntries, amount, false, false);
		resultList.forEach(e -> {
		    cumulatedElements.put(e, cumulatedElements.containsKey(e) ? cumulatedElements.get(e) + 1 : 1);
		});
	    }
	    List<FinderEntity> resultList = cumulatedElements.entrySet().stream()
		    .sorted((e, f) -> f.getValue().compareTo(e.getValue())).map(e -> e.getKey())
		    .collect(Collectors.toList());
	    Set<String> foundValue = new HashSet<>();
	    resultList.removeIf(e -> {
		if (!foundValue.contains(e.getPrimaryValue())) {
		    foundValue.add(e.getPrimaryValue());
		    return false;
		}
		return true;
	    });
	    return resultList;
	}
    }

}
