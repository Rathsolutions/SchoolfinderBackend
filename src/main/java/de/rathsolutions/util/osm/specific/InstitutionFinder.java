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

import de.rathsolutions.util.osm.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.structure.internalFinder.InstitutionFinderEntries;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javassist.NotFoundException;
import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

@Service
public class InstitutionFinder implements FinderService {

    @Autowired
    private InstitutionFinderEntries cachedEntries;

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
	return distanceUtil.computeLevenstheinDistance(primaryValue.getName(), cachedEntries, amount, false, false);
    }

}
