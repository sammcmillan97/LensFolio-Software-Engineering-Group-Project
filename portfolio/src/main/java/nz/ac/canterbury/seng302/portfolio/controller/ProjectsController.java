package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ProjectsController {

    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @GetMapping("/projects")
    public String projects(Model model) {
        List<ProjectEntity> projects = StreamSupport.stream(projectEntityRepository.findAll().spliterator(), false).toList();
        model.addAttribute("project1Name", projects.get(0).getProject_name());
        String project1Start = String.format("Start Date: %s", projects.get(0).getStart_date());
        model.addAttribute("project1Start", project1Start);
        String project1End = String.format("End Date: %s", projects.get(0).getEnd_date());
        model.addAttribute("project1End", project1End);
        model.addAttribute("project1Description", projects.get(0).getDescription());
        model.addAttribute("project2Name", projects.get(1).getProject_name());
        String project2Start = String.format("Start Date: %s", projects.get(1).getStart_date());
        model.addAttribute("project2Start", project2Start);
        String project2End = String.format("End Date: %s", projects.get(1).getEnd_date());
        model.addAttribute("project2End", project2End);
        model.addAttribute("project2Description", projects.get(1).getDescription());

        return "projects";
    }



    @GetMapping(path="/projects/all")
    public @ResponseBody
    Iterable<ProjectEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return projectEntityRepository.findAll();
    }

}
