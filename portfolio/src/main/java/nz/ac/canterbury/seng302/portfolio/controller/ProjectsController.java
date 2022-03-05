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
            Calendar cal = Calendar.getInstance();
            String projectName = String.format("Project %d", cal.get(Calendar.YEAR));
            Date startDate = new Date(cal.getTimeInMillis());
            cal.add(Calendar.MONTH, 8);
            Date endDate = new Date(cal.getTimeInMillis());
            String description = "";
            Long projectId = Long.valueOf(1);
            ProjectEntity defaultProject = new ProjectEntity(projectId, projectName, description, startDate, endDate);
            projectEntityRepository.save(defaultProject);
            projects = StreamSupport.stream(projectEntityRepository.findAll().spliterator(), false).toList();
        }


        model.addAttribute("projects", projects);

        return "projects";
    }



    @GetMapping(path="/projects/all")
    public @ResponseBody
    Iterable<ProjectEntity> getAllUsers() {
        // This returns a JSON or XML with the users
        return projectEntityRepository.findAll();
    }

}
