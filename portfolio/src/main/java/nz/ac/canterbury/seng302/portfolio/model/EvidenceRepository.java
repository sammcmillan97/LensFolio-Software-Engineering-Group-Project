package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Sprint, Integer> {
    Evidence findById(int id);
    List<Evidence> findByOwnerId(int ownerId);
}
