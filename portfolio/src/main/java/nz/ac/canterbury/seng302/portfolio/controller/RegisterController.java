package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    UserAccountClientService userAccountClientService;

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
            //Call the grpc
            userRegisterResponse = userAccountClientService.register(username, password, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response: ", userRegisterResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "register";
        }

        if (userRegisterResponse.getIsSuccess()){
            //implement logging in user??
            return "menu";
        } else {
            model.addAttribute("Unable to register user because: ", userRegisterResponse.getMessage());
            return "register";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
