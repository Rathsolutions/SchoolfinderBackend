package de.rathsolutions.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.rathsolutions.jpa.entity.additional.AdditionalInformation;

@Repository
public interface AdditionalInformationRepo extends JpaRepository<AdditionalInformation, Integer> {

}
