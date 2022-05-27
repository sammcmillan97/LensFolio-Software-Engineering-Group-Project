package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class DeadlineService {

    @Autowired
    private DeadlineRepository deadlineRepository;


    /**
     * Gets a list of all deadlines
     * @return the list of all the existing deadlines
     */
    public List<Deadline> getAllDeadlines() {
        return (List<Deadline>) deadlineRepository.findAll();
    }

    /**
     * Get the deadline by its id
     * @param deadlineId the id of the deadline
     * @return the deadline of the project which has the provided id
     * @throws Exception if the deadline is not found
     */
    public Deadline getDeadlineById(Integer deadlineId) throws Exception {
        Optional<Deadline> deadline = deadlineRepository.findById(deadlineId);
        if (deadline.isPresent()) {
            return deadline.get();
        } else {
            throw new Exception("Deadline not found");
        }
    }

    /**
     * Get the list of deadlines by providing thier parent project id
     * @param deadlineProjectId the id of the project the deadline belongs to
     * @return the list of deadlines of the project
     */
    public List<Deadline> getByDeadlineParentProjectId(int deadlineProjectId) {
        return deadlineRepository.findByDeadlineParentProjectId(deadlineProjectId);
    }

    /**
     * Deletes the deadline from the repository using the provided id
     * @param deadlineId the id of the deadline to be deleted
     */
    public void deleteDeadlineById(int deadlineId) {
        deadlineRepository.deleteById(deadlineId);
    }

    /**
     * Saves the provided deadline into the repository
     */
    public Deadline saveDeadline(Deadline deadline) {
        return deadlineRepository.save(deadline);
    }
}
