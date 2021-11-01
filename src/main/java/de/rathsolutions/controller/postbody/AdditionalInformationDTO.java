package de.rathsolutions.controller.postbody;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInformationDTO {

    private int id;

    private String value;

    private String type;
}
