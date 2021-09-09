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
package de.rathsolutions.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import de.rathsolutions.controller.postbody.ProjectDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Project {

    @Id
    @GeneratedValue
    private long id;

    @NonNull
    @Column(unique = true)
    private String projectName;

    @NonNull
    @Lob
    private String defaultIcon;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "school_project_mapping", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "school_id"))
    private List<School> allSchools = new ArrayList<>();

    public void addSchool(School schoolToAdd) {
	this.allSchools.add(schoolToAdd);
    }

    public void removeSchool(School schoolToAdd) {
	this.allSchools.remove(schoolToAdd);
    }

    public ProjectDTO convertToDto() {
	return new ProjectDTO(String.valueOf(this.id), this.projectName, this.defaultIcon);
    }
}
