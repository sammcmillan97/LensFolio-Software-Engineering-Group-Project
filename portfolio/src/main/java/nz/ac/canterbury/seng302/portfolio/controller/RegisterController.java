package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class RegisterController {

    UserAccountService userAccountService;

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
        UserRegisterResponse registerResponse;

        //Need to add check to check if username is already in use, and any other validation

        try {
            UserRegisterRequest registerRequest = UserRegisterRequest.newBuilder()
                    .setUsername(username)
                    .setEmail(email)
                    .setPassword(password)
                    .setFirstName(firstName)
                    .setMiddleName(middleName)
                    .setLastName(lastName)
                    .setNickname(nickname)
                    .setPersonalPronouns(pronouns)
                    .setBio(bio)
                    .build();

            //Call the grpc
            UserRegisterResponse userRegisterResponse = userAccountService.Register(request);

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "register";
        }

        return "menu";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
