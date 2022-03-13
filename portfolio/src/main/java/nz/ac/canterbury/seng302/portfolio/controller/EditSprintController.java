package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {
    private String sprintName = "Sprint 1";
    private String sprintStartDate = "04/Mar/2022";
    private String sprintEndDate = "25/Mar/2022";
    private String sprintDescription = "This is the first sprint.";

    private void setSprintName(String name) {
        this.sprintName = name;
    }
    private void setSprintStartDate(String date) {
        this.sprintStartDate = date;
    }
    private void setSprintEndDate(String date) {
        this.sprintEndDate = date;
    }
    private void setSprintDescription(String description) {
        this.sprintDescription = description;
    }

    @GetMapping("/edit-sprint")
    public String sprintForm(Model model) {
        /* Add sprint details to the model */
        model.addAttribute("sprintLabel", "Sprint 1");
        model.addAttribute("sprintName", this.sprintName);
        model.addAttribute("sprintStartDate", this.sprintStartDate);
        model.addAttribute("sprintEndDate", this.sprintEndDate);
        model.addAttribute("sprintDescription", this.sprintDescription);


        /* Return the name of the Thymeleaf template */
        return "editSprint";
    }

    @PostMapping("/edit-sprint")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDate,
            @RequestParam(value="sprintEndDate") String sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) {
        this.sprintName = sprintName;
        this.sprintStartDate = sprintStartDate;
        this.sprintEndDate = sprintEndDate;
        this.sprintDescription = sprintDescription;
        return "redirect:/edit-sprint";
    }

}
