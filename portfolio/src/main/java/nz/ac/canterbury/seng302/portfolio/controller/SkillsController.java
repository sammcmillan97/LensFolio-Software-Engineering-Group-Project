package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * The controller for handling backend of the skills page
 */
@Controller
public class SkillsController {
    @Autowired
    EvidenceService evidenceService;

    @GetMapping("/portfolio/skill")
    public String getEvidenceWithSkill(
                                        @AuthenticationPrincipal AuthState principal,
                                        @RequestParam("skill") String skill,
                                        Model model) {
        List<Evidence> evidence = evidenceService.retrieveEvidenceBySkill(skill);


        return "skills";
    }
}
