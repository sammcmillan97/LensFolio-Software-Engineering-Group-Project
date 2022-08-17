package nz.ac.canterbury.seng302.portfolio.repository.evidence;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Evidence findById(int id);
    List<Evidence> findByProjectId(int projectId);
    List<Evidence> findByOwnerIdAndProjectIdOrderByDateDescIdDesc(int ownerId, int projectId);

    // Get evidence with no categories
    List<Evidence> findByOwnerIdAndProjectIdAndCategoriesIsNullOrderByDateDescIdDesc(int ownerId, int projectId);

    //Get evidence by skill, might need to be updated to include projectId too
    List<Evidence> findBySkillsAndProjectIdOrderByDateDescIdDesc(String skill, int projectId);

    List<Evidence> findByOwnerIdAndProjectIdAndSkillsIsNullOrderByDateDescIdDesc(int ownerId, int projectId);
}
