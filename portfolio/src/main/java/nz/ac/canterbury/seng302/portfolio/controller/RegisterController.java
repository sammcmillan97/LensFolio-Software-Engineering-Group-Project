package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;


@Controller
public class RegisterController {

    @Autowired
    private AuthenticateClientService authenticateClientService;

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
                loginReply = authenticateClientService.authenticate(username, password);
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
            model.addAttribute("registerMessage", "");
            model.addAttribute("registerMessage", userRegisterResponse.getMessage());
            return "register";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
