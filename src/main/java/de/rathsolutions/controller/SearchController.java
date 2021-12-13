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
package de.rathsolutions.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import de.rathsolutions.util.osm.pojo.CitySearchEntity;
import de.rathsolutions.util.osm.pojo.FinderEntity;
import de.rathsolutions.util.osm.pojo.InstitutionSearchEntity;
import de.rathsolutions.util.osm.pojo.StreetCitySearchEntity;
import de.rathsolutions.util.osm.specific.InstitutionFinder;
import de.rathsolutions.util.osm.specific.OsmPOICityOnlyParser;
import de.rathsolutions.util.osm.specific.OsmStreetParser;
import io.swagger.v3.oas.annotations.Operation;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/finder")
@Slf4j
public class SearchController {
    @Autowired
    private OsmPOICityOnlyParser osmCityParser;

    @Autowired
    private OsmStreetParser osmStreetParser;

    @Autowired
    private InstitutionFinder institutionFinder;

    @Operation(summary = "searches institutions by their content in database")
    @GetMapping("/search/findGeneralInstitutionContentInDatabase")
    public ResponseEntity<List<FinderEntity>> findGeneralInstitutionContentInDatabase(
	    @RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "1") int amount) {
	try {
	    return ResponseEntity.ok(institutionFinder.find(new InstitutionSearchEntity(query), amount));
	} catch (OperationNotSupportedException | ParserConfigurationException | SAXException | IOException
		| NotFoundException | TransformerException | InterruptedException | ExecutionException e) {
	    log.error(e.getMessage());
	    return ResponseEntity.notFound().build();
	}
    }

    @Operation(summary = "searches cities by their names")
    @GetMapping("/search/findCityByName")
    public ResponseEntity<List<FinderEntity>> findCityByName(@RequestParam(defaultValue = "") String name,
	    @RequestParam(defaultValue = "1") int amount) {
	try {
	    List<FinderEntity> resultsByName = osmCityParser.find(new CitySearchEntity(name), amount);
	    return ResponseEntity.ok().header("Copyright", "This list was generated using Open Street Maps Data")
		    .body(resultsByName);
	} catch (ParserConfigurationException | SAXException | IOException | NotFoundException | TransformerException
		| InterruptedException | ExecutionException | OperationNotSupportedException e) {
	    log.error(e.getMessage());
	    return ResponseEntity.notFound().build();
	}
    }

    @Operation(summary = "searches for streets in cities by their names")
    @GetMapping("/search/findCityStreetPositionByName")
    public ResponseEntity<List<FinderEntity>> findCityStreetPositionByName(@RequestParam(defaultValue = "") String city,
	    @RequestParam(defaultValue = "") String street, @RequestParam(required = false) String housenumber,
	    @RequestParam(defaultValue = "1") int amount) {
	try {
	    return ResponseEntity.ok().header("Copyright", "This list was generated using Open Street Maps Data")
		    .body(osmStreetParser.find(new StreetCitySearchEntity(city, street, housenumber), amount));
	} catch (OperationNotSupportedException | ParserConfigurationException | SAXException | IOException
		| NotFoundException | TransformerException | InterruptedException | ExecutionException e) {
	    log.error(e.getMessage());
	    return ResponseEntity.notFound().build();
	}
    }
}
