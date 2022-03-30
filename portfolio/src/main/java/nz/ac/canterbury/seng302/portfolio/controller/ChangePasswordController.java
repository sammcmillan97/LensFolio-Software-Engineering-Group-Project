package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ChangePasswordResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the change user password page
 */
@Controller
public class ChangePasswordController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Get mapping to return change password page
     * @param principal
     * @param model
     * @return change password page
     */
    @GetMapping("/changePassword")
    public String securitySettings(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        return "changePassword";
    }

    /**
     * Post request to change user password
     * @param principal
     * @param oldPassword User's current password
     * @param newPassword User's new password
     * @param model
     * @return change password page
     */
    @PostMapping("/changePassword")
    public String changePassword(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="oldPassword") String oldPassword,
            @RequestParam(name="newPassword") String newPassword,
            Model model ) {

        //Get current user ID
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);

        ChangePasswordResponse changePasswordResponse;
        //Try to connect to IDP to submit password response
        try {
            changePasswordResponse = userAccountClientService.changeUserPassword(id, oldPassword, newPassword);
        } catch(Exception e) {
            model.addAttribute("failure", "Error connecting to Identity Provider");
            return "changePassword";
        }
        //Success or fail the user will be returned to the security menu with appropriate feedback message displayed
        if (changePasswordResponse.getIsSuccess()) {
            model.addAttribute("success", changePasswordResponse.getMessage());
        } else {
            StringBuilder failure = new StringBuilder();
            for (ValidationError error: changePasswordResponse.getValidationErrorsList()) {
                failure.append("\n");
                failure.append(error.getErrorText());
            }
            model.addAttribute("failure", failure);
        }
        return "changePassword";
    }

}


