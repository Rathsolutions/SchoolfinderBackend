package de.rathsolutions.jpa.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Project {

    private long id;

    private String projectName;

    @Lob
    private String defaultIcon;

    @OneToMany(mappedBy = "project")
    private List<School> allSchools;
}
