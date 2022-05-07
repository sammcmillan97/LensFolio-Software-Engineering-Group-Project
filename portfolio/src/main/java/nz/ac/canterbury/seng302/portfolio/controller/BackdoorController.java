package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Controller for controlling backdoor requests (ADMIN AND TESTING PURPOSES ONLY)
 */
@Controller
public class BackdoorController {

    @Autowired
    ProjectService projectService;
    @Autowired
    SprintService sprintService;

    /**
     * Get request to add a default projects and sprints
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Projects page
     */
    @GetMapping(value="/projects/all")
    public String addEntitiesForDemo(Model model) {

        Calendar cal = Calendar.getInstance();
        Date startDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 8);
        Date endDate = new Date(cal.getTimeInMillis());

        Project project1 = new Project("Project 1", "This is a project", startDate, endDate);
        Project project2 = new Project("Project 2", "This is another project", startDate, endDate);
        projectService.saveProject(project1);
        projectService.saveProject(project2);

        cal.add(Calendar.MONTH, -8);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        startDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.WEEK_OF_YEAR, 3);
        endDate = new Date(cal.getTimeInMillis());

        Sprint sprint1 = new Sprint(project1.getId(), "Sprint Name", 1, "This is a sprint", startDate, endDate);
        Sprint sprint2 = new Sprint(project2.getId(), "Sprint Name", 2, "This is a sprint", startDate, endDate);

        cal.add(Calendar.DAY_OF_MONTH, 1);
        startDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.WEEK_OF_YEAR, 3);
        endDate = new Date(cal.getTimeInMillis());

        Sprint sprint3 = new Sprint(project1.getId(), "Sprint Name", 1, "This is a sprint", startDate, endDate);
        Sprint sprint4 = new Sprint(project2.getId(), "Sprint Name", 2, "This is a sprint", startDate, endDate);

        List<Sprint> project1Sprints = new ArrayList<>();
        List<Sprint> project2Sprints = new ArrayList<>();

        project1Sprints.add(sprint1);
        project1Sprints.add(sprint2);
        project2Sprints.add(sprint3);
        project2Sprints.add(sprint4);

        sprintService.saveSprint(sprint1);
        sprintService.saveSprint(sprint2);
        sprintService.saveSprint(sprint3);
        sprintService.saveSprint(sprint4);

        return "redirect:projects";

    }
}
