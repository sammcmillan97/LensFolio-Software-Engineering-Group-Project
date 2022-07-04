package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<Milestone> getByParentProjectId(int projectId) {
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
     * Delete the milestone by id
     * @param milestoneId the id of the milestone
     */
    public void deleteMilestoneById(int milestoneId) {
        projectEditsService.refreshProject(milestoneRepository.findById(milestoneId).getMilestoneParentProjectId());
        milestoneRepository.deleteById(milestoneId);
    }
}
