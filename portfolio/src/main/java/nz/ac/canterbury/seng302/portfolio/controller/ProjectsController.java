package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.sql.Date;

/**
 * Controller for the projects page. Has various end points for interacting with the projects stored in the database.
 */
@Controller
public class ProjectsController {

    /**
     * Repository which allows the controller to interact with the database.
     */
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    /**
     * GET endpoint for projects. Returns the projects html page to the client with relevant projects data from the
     * database. If no projects exist in the database a default project is created.
     * @param model Allows addition of objects to the projects html page.
     * @return The projects html page with relevant projects data.
     */
    @GetMapping("/projects")
    public String projects(Model model) {
        List<Project> projects = StreamSupport.stream(projectRepository.findAll().spliterator(), false).toList();

        if (projects.size() < 1) {
            Project defaultProject = new Project();
            projectRepository.save(defaultProject);
            projects = StreamSupport.stream(projectRepository.findAll().spliterator(), false).toList();
        }


        model.addAttribute("projects", projects);

        return "projects";
    }

    /**
     * Delete endpoint for projects. Takes id parameter from http request and deletes the corresponding project from
     * the database.
     * @param id ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /projects.
     */
    @DeleteMapping(value="/projects")
    public String deleteProjectById(@RequestParam(name="id") int id) {
        projectRepository.deleteById(id);
        return "redirect:/projects";
    }

    @PostMapping(value="/projects")
    public String editProjectById(@RequestParam(name = "projectId", defaultValue = "-1") int projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        if (projectId == -1) {
            Project newProject = new Project(projectName, projectDescription, projectStartDate, projectEndDate);
            projectRepository.save(newProject);
        } else {
            Project existingProject = projectRepository.findById(projectId);
            existingProject.setName(projectName);
            existingProject.setStartDate(projectStartDate);
            existingProject.setEndDate(projectEndDate);
            existingProject.setDescription(projectDescription);
            projectRepository.save(existingProject);
        }

        return "redirect:/projects";
    }

//    @GetMapping(value="/projects/all")
//    public String addEntitiesForDemo(Model model) {
//
//        Calendar cal = Calendar.getInstance();
//        Date startDate = new Date(cal.getTimeInMillis());
//        cal.add(Calendar.MONTH, 8);
//        Date endDate = new Date(cal.getTimeInMillis());
//
//        Project project1 = new Project("Project 1", "This is a project", startDate, endDate);
//        Project project2 = new Project("Project 2", "This is another project", startDate, endDate);
//
//        cal.add(Calendar.MONTH, -8);
//        cal.add(Calendar.DAY_OF_MONTH, 1);
//        startDate = new Date(cal.getTimeInMillis());
//        cal.add(Calendar.WEEK_OF_YEAR, 3);
//        endDate = new Date(cal.getTimeInMillis());
//
//        Sprint sprint1 = new Sprint(project1, "Sprint 1", "Sprint Name", "This is a sprint", startDate, endDate);
//        Sprint sprint2 = new Sprint(project2, "Sprint 1", "Sprint Name", "This is a sprint", startDate, endDate);
//
//        cal.add(Calendar.DAY_OF_MONTH, 1);
//        startDate = new Date(cal.getTimeInMillis());
//        cal.add(Calendar.WEEK_OF_YEAR, 3);
//        endDate = new Date(cal.getTimeInMillis());
//
//        Sprint sprint3 = new Sprint(project1, "Sprint 2", "Sprint Name", "This is a sprint", startDate, endDate);
//        Sprint sprint4 = new Sprint(project2, "Sprint 2", "Sprint Name", "This is a sprint", startDate, endDate);
//
//        Set<Sprint> project1Sprints = new HashSet<>();
//        Set<Sprint> project2Sprints = new HashSet<>();
//
//        project1Sprints.add(sprint1);
//        project1Sprints.add(sprint3);
//        project2Sprints.add(sprint2);
//        project2Sprints.add(sprint4);
//
//        project1.setSprints(project1Sprints);
//        project2.setSprints(project2Sprints);
//
//        projectRepository.save(project1);
//        projectRepository.save(project2);
//        sprintRepository.save(sprint1);
//        sprintRepository.save(sprint2);
//        sprintRepository.save(sprint3);
//        sprintRepository.save(sprint4);
//
//        return "redirect:/projects";
//
//    }
}
