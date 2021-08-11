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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.Person;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

    public Optional<Person> findByPrenameAndLastnameAndEmailAndPhoneNumber(String prename, String lastname,
	    String email, String phoneNumber);

    public Optional<Person> findByEmail(String email);

    public Optional<Person> findByPrenameAndLastname(String prename, String lastname);

    public Optional<Person> findByPrenameAndLastnameAndEmail(String prename, String lastname, String email);

    public boolean existsByPrenameAndLastnameAndEmailAndPhoneNumber(String prename, String lastname, String email,
	    String phonenumber);

    public int countByEmail(String email);

    public int countByPrenameAndLastname(String prename, String lastname);

    public int countByPrenameAndLastnameAndEmail(String prename, String lastname, String email);

    public List<Person> findByPrenameAndLastnameAndEmailContaining(String prename, String lastname, String email,
	    Pageable page);
}
