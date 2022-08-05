package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Integer> {
    Evidence findById(int id);
    List<Evidence> findByProjectId(int projectId);
    List<Evidence> findByOwnerIdAndProjectId(int ownerId, int projectId);
    @Query("select e from Evidence e where e.categories = :categroy and e.ownerId = :userId and e.projectId = :projectId")
    List<Evidence> findByCategories(@Param("category") Categories category, @Param("userId") int userId, @Param("projectId") int projectId);
}
