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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.Criteria;

@Repository
public interface CriteriaRepo extends JpaRepository<Criteria, Long> {

    public List<Criteria> findAllByCriteriaNameIn(List<String> criteriaNameList);

    public List<Criteria> findAllByCriteriaNameIn(List<String> criteriaNameList, Pageable page);

    public List<Criteria> findAllByCriteriaNameContaining(String criteriaNameList, Pageable page);

    public List<Criteria> findAllByIdIn(List<Long> criteriaIdList);

    public Criteria findByCriteriaName(String criteriaName);

    public boolean existsByCriteriaName(String criteriaName);
}
