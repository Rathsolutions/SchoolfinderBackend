package de.rathsolutions.util.osm.specific;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.generic.LevenstheinDistanceUtil;
import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.structure.internalFinder.InstitutionFinderEntries;
import javassist.NotFoundException;

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
