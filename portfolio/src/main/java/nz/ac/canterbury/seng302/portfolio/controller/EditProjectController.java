package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    /* Create default project. TODO: use database to check for this*/
    Project project = new Project("Project 2022", "", "04/Mar/2022",
                                  "04/Nov/2022");

    @GetMapping("/edit-project")
    public String projectForm(Model model) {
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectStartDate", project.getStartDateString());
        model.addAttribute("projectEndDate", project.getEndDateString());
        model.addAttribute("projectDescription", project.getDescription());


        /* Return the name of the Thymeleaf template */
        return "editProject";
    }

    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        project.setName(projectName);
        project.setStartDateString(projectStartDate);
        project.setEndDateString(projectEndDate);
        project.setDescription(projectDescription);
        return "redirect:/edit-project";
    }

}
