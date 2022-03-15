package nz.ac.canterbury.seng302.portfolio.repositories;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface ProjectEntityRepository extends CrudRepository<ProjectEntity, Long> {
}