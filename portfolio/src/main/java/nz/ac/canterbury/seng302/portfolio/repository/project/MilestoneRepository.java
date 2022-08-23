package nz.ac.canterbury.seng302.portfolio.repository.project;

import nz.ac.canterbury.seng302.portfolio.model.project.Milestone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends CrudRepository<Milestone, Integer> {
    Milestone findById(int id);
    List<Milestone> findByMilestoneParentProjectIdOrderByMilestoneDate(int milestoneParentProjectId);
}
