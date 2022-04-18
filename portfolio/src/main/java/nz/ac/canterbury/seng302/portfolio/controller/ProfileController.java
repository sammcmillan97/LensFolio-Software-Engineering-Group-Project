package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The controller for handling backend of the profile page
 */
@Controller
public class ProfileController {

    @Autowired
    private UserAccountClientService userService;

    /**
     * Display the user's profile page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The string "profile"
     */
    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {

        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        User user = userService.getUserAccountById(id);

        model.addAttribute("user", user);
        model.addAttribute("name", user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
        return "profile";
    }
}

