package de.rathsolutions.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Polygon;

@Entity
@Getter
@Setter
public class District {

    @Id
    @GeneratedValue
    private long id;

    @Column(unique = true)
    private String districtName;

    private Polygon districtArea;

}
