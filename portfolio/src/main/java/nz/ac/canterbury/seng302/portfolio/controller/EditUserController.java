package nz.ac.canterbury.seng302.portfolio.controller;


import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EditUserController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    @Autowired
    private UserAccountClientService userAccountClientService;

    @GetMapping("/editUser")
    public String editUser(
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
        model.addAttribute("userId", id);
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUser(@AuthenticationPrincipal AuthState principal,
                           @RequestParam(name="username") String username,
                           @RequestParam(name="email") String email,
                           @RequestParam(name="firstName") String firstName,
                           @RequestParam(name="middleName") String middleName,
                           @RequestParam(name="lastName") String lastName,
                           @RequestParam(name="nickname") String nickname,
                           @RequestParam(name="pronouns") String pronouns,
                           @RequestParam(name="bio") String bio,
                           Model model) {

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        System.out.println("called edit user");
        EditUserResponse editUserResponse;

        //some validation, could use more
        if (email.isBlank() || firstName.isBlank() || middleName.isBlank() || lastName.isBlank()){
            model.addAttribute("errorMessage", "Oops! Please make sure that spaces are not used in required fields");
            return "editUser";
        }

        try {
            //Call the grpc with users validated params
            editUserResponse = userAccountClientService.editUser(id, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response", editUserResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "register";
        }

        if (editUserResponse.getIsSuccess()){
            UserResponse user = userAccountClientService.getUserAccountById(id);
            model.addAttribute("user", user);
            model.addAttribute("userId", id);
            return "/profile";
        } else {
            System.out.println("error, error");
            model.addAttribute("editMessage", "");
            model.addAttribute("editMessage", editUserResponse.getMessage());
            return "/editUser";
        }
    }
}