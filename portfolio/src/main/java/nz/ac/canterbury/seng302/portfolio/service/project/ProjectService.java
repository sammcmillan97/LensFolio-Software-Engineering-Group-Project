package nz.ac.canterbury.seng302.portfolio.service.project;

import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.repository.projcet.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectEditsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

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
        String message = "Project "+ project.getId() + " saved successfully";
        PORTFOLIO_LOGGER.info(message);
        return repository.save(project);
    }

    public void deleteProjectById(int id) throws NoSuchElementException {
        try {
            repository.deleteById(id);
            projectEditsService.refreshProject(id);
            String message = "Project "+ id + " deleted successfully";
            PORTFOLIO_LOGGER.info(message);
        } catch (EmptyResultDataAccessException e) {
            String message = "Project "+ id + " not found to delete";
            PORTFOLIO_LOGGER.error(message);
            throw new NoSuchElementException("No project found to delete");
        }
    }

    /**
     * Checks for a valid title,
     * can be used in all children (e.g., deadline, milestone, events, evidence, groups)
     * Allows all Characters from and Language (L), Numbers(N), Punctuation (P), Whitespace (Z)
     * (title).isBlank(); will need to be tested elsewhere
     * @param title being validated
     * @return true if valid, false if not
     */
    public boolean titleValid(String title) {
        String regex = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";
        Pattern pattern = Pattern.compile(
                regex,
                Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(title);
        String result = matcher.replaceAll("");
        return result.length() == title.length();
    }
}
