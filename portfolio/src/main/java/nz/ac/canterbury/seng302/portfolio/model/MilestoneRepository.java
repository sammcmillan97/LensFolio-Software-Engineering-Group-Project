package nz.ac.canterbury.seng302.portfolio.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Integer> {
    List<Milestone> findByMilestoneName(String milestoneName);
    Milestone findById(int id);
    List<Milestone> findByMilestoneParentProjectIdOrderByMilestoneDate(int milestoneParentProjectId);
}
