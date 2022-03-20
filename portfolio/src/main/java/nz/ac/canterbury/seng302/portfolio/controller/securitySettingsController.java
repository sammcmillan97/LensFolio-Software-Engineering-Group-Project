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
public class securitySettingsController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @GetMapping("/securitySettings")
    public String securitySettings(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        return "securitySettings";
    }

    @PostMapping("/securitySettings")
    public String changePassword(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="oldPassword") String oldPassword,
            @RequestParam(name="newPassword") String newPassword,
            Model model ) {
        //NEED TO DISCUSS VALIDATION WITH TEAM, FRONT END OR BACK END

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        ChangePasswordResponse changePasswordResponse;

        try {
            changePasswordResponse = userAccountClientService.changeUserPassword(id, oldPassword, newPassword);
            model.addAttribute("Reponse", changePasswordResponse.getMessage());
        } catch(Exception e) {
            model.addAttribute("errorMessage", e);
            return "securitySettings";
        }
        System.out.println(changePasswordResponse.getMessage());
        return "securitySettings";

    }

}


