package de.rathsolutions.controller.postbody;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;

@Getter
@Setter
public class AddNewDistrictPostbody {

    private long id;

    private String name;

    private List<Coordinate> pointList;

}
