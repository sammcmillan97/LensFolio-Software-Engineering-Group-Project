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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

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
    public String edit(HttpServletRequest request,
                           HttpServletResponse response,
                           @RequestParam(name="userId") int userId,
                           @RequestParam(name="username") String username,
                           @RequestParam(name="email") String email,
                           @RequestParam(name="password") String password,
                           @RequestParam(name="firstName") String firstName,
                           @RequestParam(name="middleName") String middleName,
                           @RequestParam(name="lastName") String lastName,
                           @RequestParam(name="nickname") String nickname,
                           @RequestParam(name="pronouns") String pronouns,
                           @RequestParam(name="bio") String bio,
                           Model model) {

        EditUserResponse editUserResponse;

        //some validation, could use more
        if (email.isBlank() || firstName.isBlank() || middleName.isBlank() || lastName.isBlank()){
            model.addAttribute("errorMessage", "Oops! Please make sure that spaces are not used in required fields");
            return "editUser";
        }

        try {
            //Call the grpc with users validated params
            editUserResponse = userAccountClientService.editUser(userId, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response: ", editUserResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "register";
        }



        if (editUserResponse.getIsSuccess()){
            AuthenticateResponse editReply;
            try {
                editReply = authenticateClientService.authenticate(username.toLowerCase(Locale.ROOT), password);
            } catch (StatusRuntimeException e){
                model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
                return "login";
            }
            if (editReply.getSuccess()) {
                var domain = request.getHeader("host");
                CookieUtil.create(
                        response,
                        "lens-session-token",
                        editReply.getToken(),
                        true,
                        5 * 60 * 60, // Expires in 5 hours
                        domain.startsWith("localhost") ? null : domain
                );
                return "redirect:/profile";
            } else {
                model.addAttribute("loginMessage", editReply.getMessage());
                return "login";
            }
        } else {
            model.addAttribute("editMessage", "");
            model.addAttribute("editMessage", editUserResponse.getMessage());
            return "/editUser";
        }
    }
}
