package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectEditsService projectEditsService;

    /**
     * Get list of all projects
     */
    public List<Project> getAllProjects() {
        return (List<Project>) repository.findAll();
    }

    /**
     * Get project by id
     */
    public Project getProjectById(Integer id) throws NoSuchElementException {
        Optional<Project> project = repository.findById(id);
        if(project.isPresent()) {
            return project.get();
        }
        else
        {
            throw new NoSuchElementException("Project not found");
        }
    }

    public Project saveProject(Project project) {
        projectEditsService.refreshProject(project.getId());
        return repository.save(project);
    }

    public void deleteProjectById(int id) throws NoSuchElementException {
        try {
            repository.deleteById(id);
            projectEditsService.refreshProject(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("No project found to delete");
        }

    }
}
