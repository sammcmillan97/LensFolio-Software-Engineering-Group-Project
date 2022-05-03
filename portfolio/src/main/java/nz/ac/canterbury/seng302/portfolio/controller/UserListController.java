package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class UserListController {

    @Autowired
    private UserAccountClientService userAccountClientService;
    
    /**
     * Gets the mapping to the list of users page html and renders it
     * @param principal The authentication state of the user
     * @param model The model of the html page for the list of users
     * @return The mapping to the list of users html page.
     */
    @GetMapping("/userList")
    public String userList(@AuthenticationPrincipal AuthState principal,
                           Model model) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        ArrayList<UserResponse> users;
        users = new ArrayList<>();
        users.add(userAccountClientService.getUserAccountById(1));
        users.add(userAccountClientService.getUserAccountById(2));
        users.add(userAccountClientService.getUserAccountById(3));
        model.addAttribute("users", users);
        return "userList";
    }

}
