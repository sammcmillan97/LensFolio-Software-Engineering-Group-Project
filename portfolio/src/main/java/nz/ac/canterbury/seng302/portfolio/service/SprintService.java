package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class SprintService {
    @Autowired
    private SprintRepository repository;
    @Autowired
    private ProjectService projectService;

    /**
     * Get list of all sprints
     */
    public List<Sprint> getAllSprints() {
        return (List<Sprint>) repository.findAll();
    }

    /**
     * Get sprint by id
     */
    public Sprint getSprintById(Integer id) throws Exception {
        Optional<Sprint> sprint = repository.findById(id);
        if(sprint.isPresent()) {
            return sprint.get();
        }
        else
        {
            throw new Exception("Sprint not found");
        }
    }

    public List<Sprint> getByParentProjectId(int projectId) {
        return repository.findByParentProjectId(projectId);
    }

    public Map<Integer, List<Sprint>> getAllByParentProjectId() {
        List<Project> projects = projectService.getAllProjects();

        Map<Integer, List<Sprint>> sprintsByParentProject = new HashMap<>();
        for (Project project : projects) {
            int projectId = project.getId();
            sprintsByParentProject.put(projectId, getByParentProjectId(projectId));
        }

        return sprintsByParentProject;
    }

    public Sprint saveSprint(Sprint sprint) {
        return repository.save(sprint);
    }

    public void deleteById(int sprintId) {
        repository.deleteById(sprintId);
    }

    public void updateStartDate(int sprintId, Date newDate) throws Exception {
        List<Sprint> sprints = getAllSprints();
        sprints.sort(Comparator.comparing(Sprint::getNumber));
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();

        for (Sprint sprint :sprints) {
            if ((sprint.getNumber() < sprintToChange.getNumber()) && (newDate.compareTo(sprint.getEndDate()) <= 0)) {
                throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
            }
        }

        if (newDate.compareTo(sprintToChange.getEndDate()) > 0) {
            throw new UnsupportedOperationException("Sprint start date must not be after end date");
        } else if (newDate.compareTo(projectStartDate) < 0 || newDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException(("Sprint start date must be within project dates"));
        } else {
            sprintToChange.setStartDate(newDate);
            saveSprint(sprintToChange);
        }
    }

    public void updateEndDate(int sprintId, Date newDate) throws Exception {
        List<Sprint> sprints = getAllSprints();
        sprints.sort(Comparator.comparing(Sprint::getNumber));
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();

        for (Sprint sprint :sprints) {
            if ((sprint.getNumber() > sprintToChange.getNumber()) && (newDate.compareTo(sprint.getStartDate()) >= 0)) {
                throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
            }
        }

        if (newDate.compareTo(sprintToChange.getStartDate()) < 0) {
            throw new UnsupportedOperationException("Sprint end date must not be before start date");
        } else if (newDate.compareTo(projectStartDate) < 0 || newDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException(("Sprint end date must be within project dates"));
        } else {
            sprintToChange.setEndDate(newDate);
            saveSprint(sprintToChange);
        }
    }
}
