package de.rathsolutions.jpa.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.additional.InformationType;

@Repository
public interface InformationTypeRepo extends JpaRepository<InformationType, Integer> {

    public Optional<InformationType> findOneByValue(String name);

    public boolean existsByValue(String value);
}
