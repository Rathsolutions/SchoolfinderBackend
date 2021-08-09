package de.rathsolutions.jpa.repo;

import de.rathsolutions.jpa.entity.District;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictRepo extends JpaRepository<District, Long> {

    public Optional<District> findOneByDistrictName(String name);
}
