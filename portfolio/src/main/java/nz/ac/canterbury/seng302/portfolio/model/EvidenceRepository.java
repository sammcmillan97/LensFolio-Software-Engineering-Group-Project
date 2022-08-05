package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Evidence findById(int id);
    List<Evidence> findByProjectId(int projectId);
    List<Evidence> findByOwnerId(int ownerId);
    List<Evidence> findByOwnerIdAndProjectId(int ownerId, int projectId);

    //Get evidence by skill, might need to be updated to include projectId too
    List<Evidence> findBySkills(String skill);
}
