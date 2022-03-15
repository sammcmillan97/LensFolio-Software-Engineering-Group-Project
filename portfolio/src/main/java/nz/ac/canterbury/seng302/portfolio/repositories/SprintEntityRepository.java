package nz.ac.canterbury.seng302.portfolio.repositories;

import nz.ac.canterbury.seng302.portfolio.entities.SprintEntity;
import org.springframework.data.repository.CrudRepository;

public interface SprintEntityRepository extends CrudRepository<SprintEntity, Long> {
}