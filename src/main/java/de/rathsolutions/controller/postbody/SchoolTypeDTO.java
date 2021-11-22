package de.rathsolutions.controller.postbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SchoolTypeDTO {

    private int id;

    private String schoolTypeValue;

    private int r;

    private int g;

    private int b;
}
