package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ChangePasswordResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SecuritySettingsController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Get mapping to return security setting page
     * @param principal
     * @param model
     * @return security setting page
     */
    @GetMapping("/securitySettings")
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
        return "securitySettings";
    }

    /**
     * Post request to change user password
     * @param principal
     * @param oldPassword User's current password
     * @param newPassword User's new password
     * @param model
     * @return Security settings page
     */
    @PostMapping("/securitySettings")
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

        ChangePasswordResponse changePasswordResponse;
        //Try to connect to IDP to submit password response
        try {
            changePasswordResponse = userAccountClientService.changeUserPassword(id, oldPassword, newPassword);
        } catch(Exception e) {
            model.addAttribute("failure", "Error connecting to Identity Provider");
            return "securitySettings";
        }
        //Success or fail the user will be returned to the security menu with appropriate feedback message displayed
        if (changePasswordResponse.getIsSuccess()) {
            model.addAttribute("success", changePasswordResponse.getMessage());
        } else {
            model.addAttribute("failure", changePasswordResponse.getMessage());
        }
        return "securitySettings";
    }

}


