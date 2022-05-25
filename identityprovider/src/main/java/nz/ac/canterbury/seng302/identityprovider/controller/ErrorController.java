package nz.ac.canterbury.seng302.identityprovider.controller;

import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {

    @GetMapping("/errors")
    public String forbidden () {
        return "redirect:/login";
    }
}
