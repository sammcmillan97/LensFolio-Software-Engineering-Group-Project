package nz.ac.canterbury.seng302.portfolio.service.project;

import nz.ac.canterbury.seng302.portfolio.model.project.Deadline;
import nz.ac.canterbury.seng302.portfolio.repository.project.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");


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
     * @throws NoSuchElementException if the deadline is not found
     */
    public Deadline getDeadlineById(Integer deadlineId) throws NoSuchElementException {
        Optional<Deadline> deadline = deadlineRepository.findById(deadlineId);
        if (deadline.isPresent()) {
            return deadline.get();
        } else {
            String message = "Deadline " + deadlineId + " not found.";
            PORTFOLIO_LOGGER.error(message);
            throw new NoSuchElementException(message);
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
        String message = "Deadline " + deadlineId + " deleted successfully";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Saves the provided deadline into the repository
     */
    public void saveDeadline(Deadline deadline) {
        projectEditsService.refreshProject(deadline.getDeadlineParentProjectId());
        deadlineRepository.save(deadline);
        String message = "Deadline " + deadline.getDeadlineId() + " saved";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Updates the deadline's date to a new date
     *
     * @param deadlineId      the id of the deadline to be updated
     * @param newDeadlineDate the new date the deadline should be set to
     * @throws UnsupportedOperationException if the new deadline date falls outside the project dates
     */
    public void updateDeadlineDate(int deadlineId, Date newDeadlineDate) throws UnsupportedOperationException {
        Deadline newDeadline = getDeadlineById(deadlineId);
        Date projectStartDate = projectService.getProjectById(newDeadline.getDeadlineParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(newDeadline.getDeadlineParentProjectId()).getEndDate();

        if (newDeadlineDate.compareTo(projectEndDate) > 0 || newDeadlineDate.compareTo(projectStartDate) < 0) {
            String message = "Deadline date (" + newDeadlineDate + ") must be within the project dates (" + projectStartDate + " - " + projectEndDate + ")";
            PORTFOLIO_LOGGER.error(message);
            throw new UnsupportedOperationException(message);
        } else {
            newDeadline.setDeadlineDate(newDeadlineDate);
            saveDeadline(newDeadline);
            String message = "Deadline " + deadlineId + " date changed to " + newDeadlineDate;
            PORTFOLIO_LOGGER.info(message);
        }
    }

    /**
     * Updates the deadline's date and name attributes
     * @param parentProjectId The parent project of the deadline
     * @param deadlineId The deadline ID
     * @param deadlineName The new deadline name
     * @param deadlineDate The new deadline date
     * @throws UnsupportedOperationException Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void updateDeadline(int parentProjectId, int deadlineId, String deadlineName, Date deadlineDate) throws UnsupportedOperationException {
        Deadline deadline = getDeadlineById(deadlineId);
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (deadlineDate.compareTo(projectEndDate) > 0 || deadlineDate.compareTo(projectStartDate) < 0) {
            String message = "Deadline date (" + deadlineDate + ") must be within the project dates (" + projectStartDate + " - " + projectEndDate + ")";
            PORTFOLIO_LOGGER.error(message);
            throw new UnsupportedOperationException(message);
        }
        deadline.setDeadlineDate(deadlineDate);
        deadline.setDeadlineName(deadlineName);
        saveDeadline(deadline);
        String message = "Deadline updated to have name " + deadlineName + " and date " + deadlineDate;
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Creates a new deadline with the given parameters
     * @param parentProjectId The parent project of the deadline
     * @param deadlineName The new deadline name
     * @param deadlineDate The new deadline date
     * @throws UnsupportedOperationException Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void createNewDeadline(int parentProjectId, String deadlineName, Date deadlineDate) throws UnsupportedOperationException {
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (deadlineDate.compareTo(projectEndDate) > 0 || deadlineDate.compareTo(projectStartDate) < 0) {
            String message = "Deadline date (" + deadlineDate + ") must be within the project dates (" + projectStartDate + " - " + projectEndDate + ")";
            PORTFOLIO_LOGGER.error(message);
            throw new UnsupportedOperationException(message);
        } else {
            saveDeadline(new Deadline(parentProjectId, deadlineName, deadlineDate));
            String message = "New deadline created with name " + deadlineName + " and date " + deadlineDate;
            PORTFOLIO_LOGGER.info(message);
        }
    }


}
