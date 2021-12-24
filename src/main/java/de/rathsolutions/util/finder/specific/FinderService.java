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
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import de.rathsolutions.util.finder.pojo.AbstractSearchEntity;
import de.rathsolutions.util.finder.pojo.FinderEntity;
import javassist.NotFoundException;

public interface FinderService {

    public List<FinderEntity> find(AbstractSearchEntity primaryValue, int amount)
	    throws OperationNotSupportedException, ParserConfigurationException, SAXException, IOException,
	    NotFoundException, TransformerException, InterruptedException, ExecutionException;
}
