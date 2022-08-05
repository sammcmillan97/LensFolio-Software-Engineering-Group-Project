package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Evidence findById(int id);
    List<Evidence> findByProjectId(int projectId);
    @Query("select e from Evidence e where e.skills = ?1")
    List<Evidence> findBySkill(String skill);
    List<Evidence> findByOwnerIdAndProjectId(int ownerId, int projectId);

    //Get evidence by skill, might need to be updated to include projectId too
    List<Evidence> findBySkills(String skill);
}
