package de.rathsolutions.jpa.repo;

import de.rathsolutions.jpa.entity.Project;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {

    public Optional<Project> findOneByProjectName(String name);

}
