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
package de.rathsolutions.util;

import de.rathsolutions.SpringBootMain;
import de.rathsolutions.jpa.entity.Area;
import de.rathsolutions.jpa.entity.PersonSchoolMapping;
import de.rathsolutions.jpa.entity.Project;
import de.rathsolutions.jpa.repo.AreaRepository;
import de.rathsolutions.jpa.repo.PersonSchoolMappingRepo;
import de.rathsolutions.jpa.repo.ProjectRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ContextConfiguration(classes = SpringBootMain.class)
@Transactional
@Slf4j

class SimpleTest {

	@Autowired
	private AreaRepository areaRepo;
	
	@Autowired
	private PersonSchoolMappingRepo repo;
	
	@Test
	@Disabled
	void test() {
		List<PersonSchoolMapping> findAll = repo.findAll();
		System.out.println(findAll);
	}

	@Disabled
	@Test
	void reduceMe() {
		String toReduce = "946577.4379464876 6327404.9328208035, 946952.4146503769 6327294.645554953, 947459.7360732859 6327096.128476424, 947945.000043025 6326985.841210574, 948430.2640127642 6326919.6688510645, 948893.4705293333 6326853.496491554, 949489.0217649222 6327007.898663744, 949952.2282814914 6327184.358289104, 950371.3198917207 6327140.243382764, 950922.7562209696 6326853.496491554, 951474.1925502185 6326632.921959855, 951981.5139731277 6326632.921959855, 952488.8353960367 6326699.094319364, 953106.4440847957 6326654.979413024, 953613.7655077047 6326566.749600344, 954231.3741964635 6326500.577240835, 954760.7530725426 6326191.772896456, 955268.0744954516 6325794.738739396, 955665.1086525109 6325463.876941847, 956062.1428095702 6325221.244956977, 956348.8897007797 6325177.130050637, 956812.0962173488 6325309.4747696575, 957407.6474529377 6325596.221660866, 958047.3135948665 6325882.968552076, 958929.6117216649 6326147.657990116, 959569.2778635938 6326235.8878027955, 959856.0247548033 6326257.945255965, 960297.1738182025 6326147.657990116, 960672.1505220918 6326059.428177436, 961245.6443045107 6325618.279114037, 961620.6210084 6325353.589675997, 962083.8275249691 6325287.417316487, 962635.2638542182 6325375.647129167, 962988.1831049375 6325287.417316487, 963341.1023556568 6324956.555518937, 963671.9641532062 6324581.578815049, 963936.6535912458 6324338.946830179, 964355.745201475 6324096.31484531, 964863.066624384 6323941.912673119, 965326.2731409532 6323875.74031361, 965811.5371106924 6323809.5679541, 966385.0308931113 6323721.33814142, 966848.2374096804 6323544.87851606, 967157.0417540598 6323191.959265341, 967487.9035516093 6322552.293123412, 967796.7078959887 6321934.684434653, 967973.1675213483 6321096.501214195, 968105.5122403682 6320545.064884946, 968370.2016784076 6320015.686008867, 968789.2932886368 6319486.307132788, 969362.7870710558 6318978.985709879, 969649.5339622652 6318471.664286969, 969693.6488686053 6317986.40031723, 969649.5339622652 6317434.963987982, 969627.4765090953 6316971.757471412, 969627.4765090953 6316618.838220693, 969958.3383066447 6316287.976423143, 970377.4299168739 6316111.516797784, 970642.1193549135 6315758.597547065, 970862.693886613 6315449.793202685, 971186.1984406369 6315410.211697686, 971529.5439184273 6315530.846595289, 971733.6952836 6315799.955213017, 971993.5242938198 6316236.096765885, 972392.5474166574 6316542.323813644, 972856.5277920499 6316774.31400134, 973218.4324848561 6316802.1528238645, 973515.3799251074 6316755.754786325, 973673.1332527408 6316662.958711247, 973868.0050104057 6316514.484991121, 974062.8767680705 6316421.688916042, 974285.5873482589 6316198.978335854, 974471.1794984159 6316115.461868283, 974703.1696861122 6315966.9881481575, 974879.4822287614 6315772.116390493, 975111.4724164576 6315688.599922922, 975250.6665290755 6315623.642670367, 975389.8606416932 6315549.405810305, 975668.2488669287 6315512.287380273, 975835.28180207 6315373.093267655, 976076.5515972741 6315215.339940022, 976271.423354939 6315168.941902483, 976401.3378600489 6315271.017585069, 976490.7070998896 6315247.3410860635, 976605.4555698558 6315139.342526095, 976686.4544898317 6315044.843786123, 976760.7034998097 6314943.5951361535, 976794.4530497998 6314835.596576185, 976747.2036798138 6314727.598016217, 976666.2047598377 6314646.599096241, 976672.9546698357 6314558.850266267, 976794.4530497998 6314464.351526295, 976875.4519697758 6314336.103236333, 976942.9510697558 6314194.355126375, 977017.2000797339 6313971.608096441, 977077.9492697158 6313856.859626475, 977253.4469296639 6313681.361966527, 977374.9453096279 6313627.362686543, 977482.943869596 6313478.864666587, 977590.942429564 6313404.615656609, 977732.690539522 6313310.116916637, 977908.18819947 6313215.618176664, 977989.1871194461 6313073.870066707, 978110.6854994101 6312776.874026795, 978245.6836993701 6312567.626816857, 978434.6811793143 6312371.879426914, 978670.9280292443 6312162.632216976, 978947.6743391624 6311939.885187042, 979285.1698390625 6311663.1388771245, 979683.4145289446 6311339.14319722, 979980.4105688566 6311109.646257288, 980108.6588588187 6310934.14859734, 980203.1575987907 6310718.151477404, 980216.6574187868 6310603.4030074375, 980216.6574187868 6310488.654537472, 980228.0432753845 6310066.297766641, 980267.2011090687 6309792.192930851, 980515.2007224018 6309491.982872606, 980697.9372795947 6309257.035870501, 981050.3577827525 6308995.983645939, 981246.1469511734 6308747.984032607, 981246.1469511734 6308395.563529449, 981298.3573960856 6307977.879970151, 981507.1991757347 6307769.038190502, 981729.0935666116 6307729.8803568175, 982185.9349595938 6307612.406855765, 982668.8815750321 6307481.880743485";
		String[] splitted = toReduce.split(",");
		for (int i = splitted.length - 1; i >= 0; i--) {
			System.out.print(splitted[i] + ",");
		}
	}

	@Test
	@Transactional
	@Rollback(false)
	@Disabled
	void convex() {
		Optional<Area> stuttgart = areaRepo.findById(1L);
		Optional<Area> boeblingen = areaRepo.findById(2L);
		Optional<Area> esslingen = areaRepo.findById(3L);
		Optional<Area> ludburg = areaRepo.findById(4L);
		Optional<Area> allStutt = areaRepo.findById(5L);
		List<Geometry> col = new ArrayList<>();
		col.add(stuttgart.get().getArea());
		col.add(boeblingen.get().getArea());
		col.add(esslingen.get().getArea());
		col.add(ludburg.get().getArea());
		Area area = new Area();
		area.setAreaInstitutionPosition(stuttgart.get().getAreaInstitutionPosition());
		Polygon poly = allStutt.get().getArea();
		poly = (Polygon) poly.difference(stuttgart.get().getArea());
		MultiPolygon difference = (MultiPolygon) poly.difference(boeblingen.get().getArea());
		difference = (MultiPolygon) difference.difference(esslingen.get().getArea());
		difference = (MultiPolygon) difference.difference(ludburg.get().getArea());
		int numGeometries = difference.getNumGeometries();
		int largest = 0;
		Geometry largestGeom = null;
		for (int i = 0; i < numGeometries; i++) {
			Geometry geometryN = difference.getGeometryN(i);
			if (largest < geometryN.getCoordinates().length) {
				largest = geometryN.getCoordinates().length;
				largestGeom = geometryN;
			}
		}
		System.out.println(largest);
		System.out.println(numGeometries);
		area.setArea((Polygon) largestGeom);
		area.setName("Schwäbisch Gmünd");
		areaRepo.save(area);
	}

	@Test
	@Transactional
	@Rollback(false)
	@Disabled
	void convexStutt() {
		Optional<Area> gmuend = areaRepo.findById(8L);
		Optional<Area> allStutt = areaRepo.findById(5L);
		Area area = new Area();
		area.setAreaInstitutionPosition(allStutt.get().getAreaInstitutionPosition());
		area.setArea((Polygon) allStutt.get().getArea().difference(gmuend.get().getArea()));
		area.setName("Stuttgart");
		areaRepo.save(area);
	}

	@Test
	@Transactional
	@Rollback(false)
	@Disabled
	void transform() {
		areaRepo.findAll().forEach(e -> {
			e.getArea().setSRID(3857);
			areaRepo.save(e);
		});
	}

}
