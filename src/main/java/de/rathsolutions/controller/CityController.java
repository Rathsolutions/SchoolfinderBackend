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

import de.rathsolutions.jpa.entity.OsmPOIEntity;
import de.rathsolutions.util.OsmPOICityOnlyParser;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javassist.NotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

@RestController
@RequestMapping("/api/v1/cities")
@Slf4j
public class CityController {
    @Autowired
    private OsmPOICityOnlyParser osmCityParser;

    @Operation(summary = "searches cities by their names")
    @GetMapping("/search/findCityByName")
    public ResponseEntity<List<OsmPOIEntity>> findCityByName(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "1") int amount) {
        try {
            List<OsmPOIEntity> resultsByName = osmCityParser.processOsmFile(name, amount);
            return ResponseEntity.ok()
                    .header("Copyright", "This list was generated using Open Street Maps Data")
                    .body(resultsByName);
        } catch (ParserConfigurationException | SAXException | IOException | NotFoundException
                | TransformerException | InterruptedException | ExecutionException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
