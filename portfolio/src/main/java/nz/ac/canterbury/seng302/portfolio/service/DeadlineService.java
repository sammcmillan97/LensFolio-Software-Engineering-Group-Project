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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEditsService projectEditsService;


    /**
     * Gets a list of all deadlines
     *
     * @return the list of all the existing deadlines
     */
    public List<Deadline> getAllDeadlines() {
        return (List<Deadline>) deadlineRepository.findAll();
    }

    /**
     * Get the deadline by its id
     *
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
     *
     * @param deadlineProjectId the id of the project the deadline belongs to
     * @return the list of deadlines of the project
     */
    public List<Deadline> getByDeadlineParentProjectId(int deadlineProjectId) {
        return deadlineRepository.findByDeadlineParentProjectIdOrderByDeadlineDate(deadlineProjectId);
    }

    /**
     * Deletes the deadline from the repository using the provided id
     *
     * @param deadlineId the id of the deadline to be deleted
     */
    public void deleteDeadlineById(int deadlineId) {
        if (deadlineRepository.findById(deadlineId) == null) {
            throw new UnsupportedOperationException("Deadline does not exist");
        }
        projectEditsService.refreshProject(deadlineRepository.findById(deadlineId).getDeadlineParentProjectId());
        deadlineRepository.deleteById(deadlineId);
    }

    /**
     * Saves the provided deadline into the repository
     */
    public Deadline saveDeadline(Deadline deadline) {
        projectEditsService.refreshProject(deadline.getDeadlineParentProjectId());
        return deadlineRepository.save(deadline);
    }

    /**
     * Updates the deadline's date to a new date
     *
     * @param deadlineId      the id of the deadline to be updated
     * @param newDeadlineDate the new date the deadline should be set to
     * @throws Exception if the new deadline date falls outside the project dates
     */
    public void updateDeadlineDate(int deadlineId, Date newDeadlineDate) throws Exception {
        Deadline newDeadline = getDeadlineById(deadlineId);
        Date projectStartDate = projectService.getProjectById(newDeadline.getDeadlineParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(newDeadline.getDeadlineParentProjectId()).getEndDate();

        if (newDeadlineDate.compareTo(projectEndDate) > 0 || newDeadlineDate.compareTo(projectStartDate) < 0) {
            throw new UnsupportedOperationException("Deadline date must be within the project dates");
        } else {
            newDeadline.setDeadlineDate(newDeadlineDate);
            saveDeadline(newDeadline);
        }
    }

    /**
     * Updates the deadline's date and name attributes
     * @param parentProjectId The parent project of the deadline
     * @param deadlineId The deadline ID
     * @param deadlineName The new deadline name
     * @param deadlineDate The new deadline date
     * @throws Exception Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void updateDeadline(int parentProjectId, int deadlineId, String deadlineName, Date deadlineDate) throws Exception {
        Deadline deadline = getDeadlineById(deadlineId);
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (deadlineDate.compareTo(projectEndDate) > 0 || deadlineDate.compareTo(projectStartDate) < 0) {
            throw new UnsupportedOperationException("Deadline date must be within the project dates");
        } else if (!projectService.validTitle(deadline.getDeadlineName())){

            throw new IllegalArgumentException("Deadline cannot contain special characters");
        }
        deadline.setDeadlineDate(deadlineDate);
        deadline.setDeadlineName(deadlineName);
        saveDeadline(deadline);
    }

    /**
     * Creates a new deadline with the given parameters
     * @param parentProjectId The parent project of the deadline
     * @param deadLineName The new deadline name
     * @param deadlineDate The new deadline date
     * @throws Exception Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void createNewDeadline(int parentProjectId, String deadLineName, Date deadlineDate) throws Exception {
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (deadlineDate.compareTo(projectEndDate) > 0 || deadlineDate.compareTo(projectStartDate) < 0) {
            throw new UnsupportedOperationException("Deadline date must be within the project dates");
        } else if (!projectService.validTitle(deadLineName)){
            throw new IllegalArgumentException("Deadline cannot contain special characters");
        } else {
            saveDeadline(new Deadline(parentProjectId, deadLineName, deadlineDate));
        }
    }


}
