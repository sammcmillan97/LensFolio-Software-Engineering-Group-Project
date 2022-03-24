package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class ProjectDetailsController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;

    @GetMapping("/projects/{id}")
    public String projectDetails(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable("id") String id) throws Exception {
        /* Add project details to the model */
        int projectId = Integer.parseInt(id);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
        model.addAttribute("sprints", sprintList);

        int sprintCount = sprintList.size();
        model.addAttribute("sprintCount", sprintCount);

        // Set the minimum start date for a new sprint
        // If no existing sprints
        if (sprintCount == 0) {
            // Then min start date is project start date

        } else {
            // Otherwise min start date is the day after the end of the previous sprint
            // Find previous sprint
            List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
            Sprint previousSprint;
            for (Sprint sprint : sprints) {
                if (sprint.getLabel() == "Sprint " + sprintCount) {
                    previousSprint = sprint;
                }
            }

            Date minStartDate = previousSprint.getEndDate();
             = minStartDate.toLocalDate();
            date.
        }

        model.addAttribute("newSprintStart", minStartDate);


        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
//        System.out.println(role);
//        if (role.equals("teacher")) {
//            return "teacherProjectDetails";
//        } else if (role.equals("student")) {
//            return "userProjectDetails";
//        } else {
//            System.out.println("Invalid Role");
//            //TODO error page if user has invalid role
//            return "projects";
//        }
        return "teacherProjectDetails";
    }

    @PutMapping(value="/projects")
    public String editProjectById(@RequestParam(name = "projectId", defaultValue = "-1") int projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        try {
            Project existingProject = projectService.getProjectById(projectId);
            existingProject.setName(projectName);
            existingProject.setStartDate(projectStartDate);
            existingProject.setEndDate(projectEndDate);
            existingProject.setDescription(projectDescription);
            projectService.saveProject(existingProject);
        } catch (Exception ignored) {}

        return "redirect:/projects/" + projectId;
    }

    @PutMapping(value="/projects/edit-sprint")
    public String editSprintById(@RequestParam(name = "parentProjectId", defaultValue = "-1") int parentProjectId,
                                 @RequestParam(name = "sprintId", defaultValue = "-1") int sprintId,
                                 @RequestParam(name = "sprintLabel") String sprintLabel,
                                 @RequestParam(name = "sprintName") String sprintName,
                                 @RequestParam(name = "sprintDescription") String sprintDescription,
                                 @RequestParam(name = "sprintStartDate") Date sprintStartDate,
                                 @RequestParam(name = "sprintEndDate") Date sprintEndDate,
                                 Model model) {
        if (parentProjectId == -1) {
            // Error - For now simply go back to project details
        }

        if (sprintId == -1) {
            // Id == -1 signals to create new sprint
            Sprint newSprint = new Sprint(parentProjectId, sprintName, sprintLabel, sprintDescription, sprintStartDate, sprintEndDate);
            sprintService.saveSprint(newSprint);
        } else {
            // Otherwise edit existing sprint
            try {
                Sprint existingSprint = sprintService.getSprintById(sprintId);
                existingSprint.setLabel(sprintLabel);
                existingSprint.setName(sprintName);
                existingSprint.setDescription(sprintDescription);
                existingSprint.setStartDate(sprintStartDate);
                existingSprint.setEndDate(sprintEndDate);
                sprintService.saveSprint(existingSprint);
            } catch (Exception ignored) {
                // TODO
            }
        }

        // Reload project details page
        return "redirect:/projects/" + parentProjectId;
    }

}
