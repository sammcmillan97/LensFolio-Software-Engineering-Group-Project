package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegisterController {


//    @PostMapping("/register")
//    public String registerSubmit(@ModelAttribute RegisterRequest registerRequest, Model model) {
//        model.addAttribute("registerRequest", registerRequest);
//        return "success";
//    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
}
