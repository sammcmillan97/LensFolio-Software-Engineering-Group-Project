package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.project.Sprint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends CrudRepository<Sprint, Integer> {
    List<Sprint> findBySprintName(String sprintName);
    Sprint findById(int id);
    List<Sprint> findByParentProjectId(int parentProjectId);
}
