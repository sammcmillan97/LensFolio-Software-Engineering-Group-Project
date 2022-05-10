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
    public Sprint getSprintById(Integer id) throws NoSuchElementException {
        Optional<Sprint> sprint = repository.findById(id);
        if(sprint.isPresent()) {
            return sprint.get();
        }
        else
        {
            throw new NoSuchElementException("Sprint not found");
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
}
