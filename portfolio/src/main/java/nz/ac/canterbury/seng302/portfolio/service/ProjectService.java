package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Validate titles for any titles (projects, deadlines, milestones, sprint, etc
     * @param title of object
     * @return true if valid else return if false
     */
    public boolean validTitle(String title) {
        Pattern namePattern = Pattern.compile("[a-zA-Z1-9àáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆŠŽ∂ð ,.'\\-]+");
        Matcher nameMatcher = namePattern.matcher(title);
        return nameMatcher.matches();
    }
}
