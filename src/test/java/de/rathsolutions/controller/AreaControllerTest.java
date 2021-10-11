package de.rathsolutions.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.controller.postbody.AreaDTO;
import de.rathsolutions.controller.postbody.Position;
import de.rathsolutions.jpa.repo.AreaRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Sql(scripts = "../../../data.sql")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class AreaControllerTest {

    @Autowired
    private AreaController cut;

    @Autowired
    private AreaRepository areaRepo;

    @Test
    public void testFindByName() {
	ResponseEntity<AreaDTO> findByName = cut.findByName("testarea1");
	assertAreas(findByName.getBody(), areaRepo.findById(-1L).get().convertToDTO());
    }

    @Test
    public void testFindAll() {
	ResponseEntity<List<AreaDTO>> findByName = cut.findAll();
	findByName.getBody().forEach(e -> {
	    assertAreas(areaRepo.findById(e.getId()).get().convertToDTO(), e);
	});
    }

    @Test
    public void testCreateArea() {
	AreaDTO dto = new AreaDTO();
	dto.setColor("color");
	dto.setName("name");
	dto.setId(1);
	dto.setAreaInstitutionPosition(new Position(1.1111, 2.2222));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	dto.getAreaPolygon().add(new Position(955239.3557706389, 6137926.321942585));
	dto.getAreaPolygon().add(new Position(930779.5067193825, 6177673.576650876));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	ResponseEntity<AreaDTO> result = cut.create(dto);
	assertAreas(dto, areaRepo.findById(result.getBody().getId()).get().convertToDTO());
    }

    @Test
    public void testCreateWithAlreadyExistingName() {
	AreaDTO dto = new AreaDTO();
	dto.setColor("color");
	dto.setName("testarea1");
	dto.setId(1);
	dto.setAreaInstitutionPosition(new Position(1.1111, 2.2222));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	dto.getAreaPolygon().add(new Position(955239.3557706389, 6137926.321942585));
	dto.getAreaPolygon().add(new Position(930779.5067193825, 6177673.576650876));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	ResponseEntity<AreaDTO> result = cut.create(dto);
	assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
	assertNull(result.getBody());
    }

    @Test
    public void testEditArea() {
	AreaDTO dto = new AreaDTO();
	dto.setColor("color");
	dto.setName("testarea1");
	dto.setId(-1);
	dto.setAreaInstitutionPosition(new Position(1.1111, 2.2222));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	dto.getAreaPolygon().add(new Position(955239.3557706389, 6137926.321942585));
	dto.getAreaPolygon().add(new Position(930779.5067193825, 6177673.576650876));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	ResponseEntity<AreaDTO> result = cut.edit(dto);
	assertAreas(dto, areaRepo.findById(result.getBody().getId()).get().convertToDTO());
    }

    @Test
    public void testEditWithNotExistingName() {
	AreaDTO dto = new AreaDTO();
	dto.setColor("color");
	dto.setName("not_existing");
	dto.setId(1);
	dto.setAreaInstitutionPosition(new Position(1.1111, 2.2222));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	dto.getAreaPolygon().add(new Position(955239.3557706389, 6137926.321942585));
	dto.getAreaPolygon().add(new Position(930779.5067193825, 6177673.576650876));
	dto.getAreaPolygon().add(new Position(891032.2520110907, 6143429.787979117));
	ResponseEntity<AreaDTO> result = cut.edit(dto);
	assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
	assertNull(result.getBody());
    }

    @Test
    public void testDelete() {
	cut.delete(-1);
	assertTrue(areaRepo.findById(-1L).isEmpty());
    }

    @Test
    public void testDeleteNotExistingId() {
	assertEquals(HttpStatus.NOT_FOUND, cut.delete(1).getStatusCode());
	assertTrue(areaRepo.findById(-1L).isPresent());
	assertTrue(areaRepo.findById(-2L).isPresent());
    }

    private void assertAreas(AreaDTO expected, AreaDTO actual) {
	assertEquals(expected.getId(), actual.getId());
	assertEquals(expected.getName(), actual.getName());
	assertEquals(expected.getColor(), actual.getColor());
	for (Position expectedPos : expected.getAreaPolygon()) {
	    boolean found = false;
	    for (Position actualPos : actual.getAreaPolygon()) {
		if (Math.abs(expectedPos.getLatitude() - actualPos.getLatitude()) < 0.0001
			&& Math.abs(expectedPos.getLongitude() - actualPos.getLongitude()) < 0.0001) {
		    found = true;
		}
	    }
	    if (!found) {
		fail("At least one position was not present!");
	    }
	}
	assertEquals(expected.getAreaInstitutionPosition().getLatitude(),
		actual.getAreaInstitutionPosition().getLatitude(), 0.0001);
	assertEquals(expected.getAreaInstitutionPosition().getLongitude(),
		actual.getAreaInstitutionPosition().getLongitude(), 0.0001);
    }
}
