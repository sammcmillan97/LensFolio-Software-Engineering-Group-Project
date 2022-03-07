package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @DeleteMapping(value="/projects")
    public String deleteProjectById(@RequestParam(name="id") Long id) {
        projectEntityRepository.deleteById(id);
        return "redirect:/projects";
    }



    @GetMapping(path="/projects/all")
    public @ResponseBody
    Iterable<ProjectEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return projectEntityRepository.findAll();
    }

}
