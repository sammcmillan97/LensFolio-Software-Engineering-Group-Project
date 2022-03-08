package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.RegisterRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class profileController {

    @GetMapping("/profile")
    public String register(Model model) {
        RegisterRequest user = new RegisterRequest();
        user.setUsername("test");
        model.addAttribute("user", user);
        return "profile";
    }
}

