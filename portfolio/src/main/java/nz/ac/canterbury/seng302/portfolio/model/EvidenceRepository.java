package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Evidence findById(int id);
    List<Evidence> findByProjectId(int projectId);
    List<Evidence> findByOwnerIdAndProjectId(int ownerId, int projectId);
    List<Evidence> findAll();
}
