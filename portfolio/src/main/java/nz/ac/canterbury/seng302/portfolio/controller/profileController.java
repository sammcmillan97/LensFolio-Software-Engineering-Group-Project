package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class profileController {

    @GetMapping("/profile")
    public String register() {
        return "profile";
    }
}

