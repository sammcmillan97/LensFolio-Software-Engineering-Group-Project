package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The controller for handling the backend of the register page
 */
@Controller
public class RegisterController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

    @Autowired
    UserAccountClientService userAccountClientService;

    /**
     * Register a user with the IDP.
     *
     * If the register attempt fails, displays the reason.
     * If it succeeds, logs in the user and takes them to their profile page.
     * If for some reason this login fails, takes the user to the login page.
     *
     * @param request HTTP request sent to this endpoint
     * @param response HTTP response that will be returned by this endpoint
     * @param username Username of account to register with IDP
     * @param email Email associated with username
     * @param password Password associated with username
     * @param firstName First name associated with username
     * @param middleName Middle name associated with username
     * @param lastName Last name associated with username
     * @param nickname Nickname associated with username
     * @param pronouns Personal pronouns associated with username
     * @param bio Bio associated with username
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Response generated by IdP about register attempt
     */
    @PostMapping("/register")
    public String register(HttpServletRequest request,
                           HttpServletResponse response,
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
        UserRegisterResponse userRegisterResponse;

        try {
            //Call the grpc with users validated params
            userRegisterResponse = userAccountClientService.register(username.toLowerCase(Locale.ROOT), password, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response: ", userRegisterResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "register";
        }

        if (userRegisterResponse.getIsSuccess()){
            AuthenticateResponse loginReply;
            try {
                loginReply = authenticateClientService.authenticate(username.toLowerCase(Locale.ROOT), password);
            } catch (StatusRuntimeException e){
                model.addAttribute("loginMessage", "Error connecting to Identity Provider...");
                return "login";
            }
            if (loginReply.getSuccess()) {
                var domain = request.getHeader("host");
                CookieUtil.create(
                        response,
                        "lens-session-token",
                        loginReply.getToken(),
                        true,
                        5 * 60 * 60, // Expires in 5 hours
                        domain.startsWith("localhost") ? null : domain
                );
                return "redirect:/profile";
            } else {
                model.addAttribute("loginMessage", loginReply.getMessage());
                return "login";
            }
        } else {
            // Add attributes back into the page so the user doesn't have to enter them again
            model.addAttribute("username", username);
            model.addAttribute("firstName", firstName);
            model.addAttribute("middleName", middleName);
            model.addAttribute("lastName", lastName);
            model.addAttribute("nickname", nickname);
            model.addAttribute("bio", bio);
            model.addAttribute("email", email);
            model.addAttribute("pronouns", pronouns);

            // Add errors to the page to tell the user what they need to fix
            List<ValidationError> validationErrors = userRegisterResponse.getValidationErrorsList();
            model.addAttribute("validationErrors", validationErrors);

            return "register";
        }


    }

    /**
     * Get mapping for displaying the register page
     * @return the register page
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
