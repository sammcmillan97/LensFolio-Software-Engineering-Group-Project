package nz.ac.canterbury.seng302.portfolio.service.project;

import nz.ac.canterbury.seng302.portfolio.model.project.Milestone;
import nz.ac.canterbury.seng302.portfolio.repository.project.MilestoneRepository;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MilestoneService {
    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectEditsService projectEditsService;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Get a list of all milestones
     * @return the list of all existing milestones
     */
    public List<Milestone> getAllMilestones() {
        return (List<Milestone>) milestoneRepository.findAll();
    }

    /**
     * Get the milestone by id
     * @param milestoneId the id of the milestone
     * @return the milestone which has the required id
     * @throws IllegalArgumentException when milestone is not found
     */
    public Milestone getMilestoneById(Integer milestoneId) throws IllegalArgumentException {
        Optional<Milestone> milestone = milestoneRepository.findById(milestoneId);
        if (milestone.isPresent()) {
            return milestone.get();
        } else {
            throw new IllegalArgumentException("Milestone not found");
        }
    }

    /**
     * Get milestone by parent project id
     * @param projectId the id of the project
     * @return the list of milestones by the project id
     */
    public List<Milestone> getByMilestoneParentProjectId(int projectId) {
        return milestoneRepository.findByMilestoneParentProjectIdOrderByMilestoneDate(projectId);
    }

    /**
     * Save the milestone to the repository
     */
    public void saveMilestone(Milestone milestone) {
        projectEditsService.refreshProject(milestone.getMilestoneParentProjectId());
        milestoneRepository.save(milestone);
    }

    /**
     * Deletes the milestone from the repository using the provided id
     *
     * @param milestoneId the id of the deadline to be deleted
     */
    public void deleteMilestoneById(int milestoneId) {
        if (milestoneRepository.findById(milestoneId) == null) {
            throw new UnsupportedOperationException("Milestone does not exist");
        }
        projectEditsService.refreshProject(milestoneRepository.findById(milestoneId).getMilestoneParentProjectId());
        milestoneRepository.deleteById(milestoneId);
        String message = "Milestone " + milestoneId + " deleted successfully";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Updates the milestone's date and name attributes
     * @param parentProjectId The parent project of the milestone
     * @param milestoneId The milestone ID
     * @param milestoneName The new deadline name
     * @param milestoneDate The new deadline date
     * @throws UnsupportedOperationException Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void updateMilestone(int parentProjectId, int milestoneId, String milestoneName, Date milestoneDate) throws UnsupportedOperationException {
        Milestone milestone = getMilestoneById(milestoneId);
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (milestoneDate.compareTo(projectEndDate) > 0 || milestoneDate.compareTo(projectStartDate) < 0) {
            String message = "Milestone date (" + milestoneDate + ") must be within the project dates (" + projectStartDate + " - " + projectEndDate + ")";
            PORTFOLIO_LOGGER.error(message);
            throw new UnsupportedOperationException(message);
        }
        milestone.setMilestoneDate(milestoneDate);
        milestone.setMilestoneName(milestoneName);
        saveMilestone(milestone);
        String message = "Milestone updated to have name " + milestoneName + " and date " + milestoneDate;
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Creates a new milestone with the given parameters
     * @param parentProjectId The parent project of the milestone
     * @param milestoneName The new milestone name
     * @param milestoneDate The new milestone date
     * @throws UnsupportedOperationException Throws UnsupportedOperationException is the new date doesn't fall within the parent project dates
     */
    public void createNewMilestone(int parentProjectId, String milestoneName, Date milestoneDate) throws UnsupportedOperationException {
        Project parentProject = projectService.getProjectById(parentProjectId);
        Date projectStartDate = parentProject.getStartDate();
        Date projectEndDate = parentProject.getEndDate();
        if (milestoneDate.compareTo(projectEndDate) > 0 || milestoneDate.compareTo(projectStartDate) < 0) {
            String message = "Milestone date (" + milestoneDate + ") must be within the project dates (" + projectStartDate + " - " + projectEndDate + ")";
            PORTFOLIO_LOGGER.error(message);
            throw new UnsupportedOperationException(message);
        } else {
            saveMilestone(new Milestone(parentProjectId, milestoneName, milestoneDate));
            String message = "New milestone created with name " + milestoneName + " and date " + milestoneDate;
            PORTFOLIO_LOGGER.info(message);
        }
    }
}
