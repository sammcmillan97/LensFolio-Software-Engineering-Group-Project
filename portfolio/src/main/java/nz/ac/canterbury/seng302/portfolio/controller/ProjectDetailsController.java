package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Controller
public class ProjectDetailsController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private UserAccountClientService userAccountClientService;

    @GetMapping("/projects/{id}")
    public String projectDetails(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable("id") String id) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        int projectId = Integer.parseInt(id);
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        List<Sprint> sprintList = sprintService.getByParentProjectId(projectId);
        model.addAttribute("sprints", sprintList);

        String role = userAccountClientService.getRole(principal);

        /* Return the name of the Thymeleaf template
        detects the role of the current user and returns appropriate page
        System.out.println(role);*/
        if (role.contains("teacher")) {
            return "teacherProjectDetails";
        } else {
            return "userProjectDetails";
        }
    }

    @PutMapping(value="/projects")
    public String editProjectById(@AuthenticationPrincipal AuthState principal,
                                  @RequestParam(name = "projectId", defaultValue = "-1") int projectId,
                                  @RequestParam(name = "projectName") String projectName,
                                  @RequestParam(name = "projectDescription") String projectDescription,
                                  @RequestParam(name = "projectStartDate") Date projectStartDate,
                                  @RequestParam(name = "projectEndDate") Date projectEndDate,
                                  Model model) {
        String role = userAccountClientService.getRole(principal);
        if (role.contains("teacher")) {
            try {
                Project existingProject = projectService.getProjectById(projectId);
                existingProject.setName(projectName);
                existingProject.setStartDate(projectStartDate);
                existingProject.setEndDate(projectEndDate);
                existingProject.setDescription(projectDescription);
                projectService.saveProject(existingProject);
            } catch (Exception ignored) {
            }
        }

        return "redirect:/projects/" + projectId;
    }

}
