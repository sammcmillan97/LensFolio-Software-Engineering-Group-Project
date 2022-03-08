package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Calendar;

import java.sql.Date;

@Controller
public class ProjectsController {

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @GetMapping("/projects")
    public String projects(Model model) {
//        projectEntityRepository.deleteAll(); // Use for testing if default project works
        List<ProjectEntity> projects = StreamSupport.stream(projectEntityRepository.findAll().spliterator(), false).toList();

        if (projects.size() < 1) {
            ProjectEntity defaultProject = new ProjectEntity();
            projectEntityRepository.save(defaultProject);
            projects = StreamSupport.stream(projectEntityRepository.findAll().spliterator(), false).toList();
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
    public String deleteProjectById(@RequestParam(name="id") Long id) {
        projectEntityRepository.deleteById(id);
        return "redirect:/projects";
    }

//    @GetMapping("/projects/id")
//    public String getProjectById(@RequestParam(name="id") Long id, Model model) {
//
//        model.addAttribute("project", projectEntityRepository.findById(id));
//        return "redirect:/projects";
//    }

    @PostMapping(value="/projects")
    public String editProjectById(@RequestParam(name = "projectId", defaultValue = "-1") Long projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        if (projectId == -1) {
            System.out.println(projectName);
            ProjectEntity newProject = new ProjectEntity(projectName, projectDescription, projectStartDate, projectEndDate);
            projectEntityRepository.save(newProject);
        } else {
            Optional<ProjectEntity> oldProject = projectEntityRepository.findById(projectId);
            ProjectEntity updatedProject = new ProjectEntity(projectId, projectName, projectDescription, projectStartDate, projectEndDate);
            projectEntityRepository.save(updatedProject);
        }

        return "redirect:/projects";
    }



    @GetMapping(path="/projects/all")
    public @ResponseBody
    Iterable<ProjectEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return projectEntityRepository.findAll();
    }

}
