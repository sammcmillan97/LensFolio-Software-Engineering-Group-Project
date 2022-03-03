package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProjectsController {

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @GetMapping("/projects")
    public String projects() {
        return "projects";
    }



    @GetMapping(path="/projects/all")
    public @ResponseBody
    Iterable<ProjectEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return projectEntityRepository.findAll();
    }

}
