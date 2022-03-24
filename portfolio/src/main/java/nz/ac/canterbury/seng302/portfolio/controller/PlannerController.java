package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for the project planner page
 */
@Controller
public class PlannerController {
    /**
     * Autowired sprint and project services, which handle the database calls
     */
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;

    /**
     * GET endpoint for planner page. Returns the planner html page to the client with relevant project and sprint data
     * from the database
     * @param model Allows addition of objects to the planner html page.
     * @return The planner html page with relevant project and sprint data.
     */
    @GetMapping("/planner")
    public String planner(@RequestParam(name="projectId", defaultValue = "-1") int projectId,
                          Model model) {

        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 8);


        if (projectId == -1) {
            List<Project> projects = projectService.getAllProjects();
            if (projects.size() > 0) {
                model.addAttribute("project", projects.get(0));
            } else {
                Project project = new Project("Default Project", "Random Description", startDate.getTime(), endDate.getTime());
                model.addAttribute("project", project);
            }
        } else {
            try {
                Project project = projectService.getProjectById(projectId);
                model.addAttribute("project", project);
            } catch (Exception ignored) {

            }
        }


        return "planner";
    }
}
