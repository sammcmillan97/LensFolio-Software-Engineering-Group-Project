package nz.ac.canterbury.seng302.portfolio.model;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeadlineRepository extends CrudRepository<Deadline, Integer> {
    List<Deadline> findByDeadlineName(String deadlineName);
    Deadline findById(int id);
    List<Deadline> findByDeadlineParentProjectIdOrderByDeadlineDate(int deadlineParentProjectId);
}
