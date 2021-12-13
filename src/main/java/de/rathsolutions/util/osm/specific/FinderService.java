package de.rathsolutions.util.osm.specific;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.pojo.AbstractSearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import javassist.NotFoundException;

public interface FinderService {

    public List<FinderEntity> find(AbstractSearchEntity primaryValue, int amount)
	    throws OperationNotSupportedException, ParserConfigurationException, SAXException, IOException,
	    NotFoundException, TransformerException, InterruptedException, ExecutionException;
}
