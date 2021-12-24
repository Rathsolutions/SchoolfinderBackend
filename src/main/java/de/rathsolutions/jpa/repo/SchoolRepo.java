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
package de.rathsolutions.jpa.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.Criteria;
import de.rathsolutions.jpa.entity.School;
import de.rathsolutions.jpa.entity.SchoolType;

@Repository
public interface SchoolRepo extends JpaRepository<School, Long> {

    public List<School> findDistinctByMatchingCriteriasIn(List<Criteria> criteriaName);

    public List<School> findAllByLatitudeBetweenAndLongitudeBetween(double leftLatBound, double rightLatBound,
	    double topLongBound, double bottomLongBound);

    public List<School> findAllByOrderBySchoolName();

    public List<School> findDistinctByLatitudeBetweenAndLongitudeBetweenAndMatchingCriteriasIn(double leftLatBound,
	    double rightLatBound, double topLongBound, double bottomLongBound, List<Criteria> criterias);

    public List<School> findDistinctByLatitudeBetweenAndLongitudeBetweenAndMatchingCriteriasInAndTypeIn(
	    double leftLatBound, double rightLatBound, double topLongBound, double bottomLongBound,
	    List<Criteria> criterias, List<SchoolType> schoolTypes);

    public List<School> findDistinctByLatitudeBetweenAndLongitudeBetweenAndTypeIn(double leftLatBound,
	    double rightLatBound, double topLongBound, double bottomLongBound, List<SchoolType> schoolTypes);

    public Optional<School> findOneBySchoolName(String schoolName);

}
