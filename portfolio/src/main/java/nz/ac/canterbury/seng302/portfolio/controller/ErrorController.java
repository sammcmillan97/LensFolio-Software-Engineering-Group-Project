package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

    @Autowired
    UserAccountClientService userAccountClientService;

    @RequestMapping(value="/errors", method = {RequestMethod.GET, RequestMethod.POST})
    public String forbidden(@AuthenticationPrincipal AuthState principal) {
        if (!userAccountClientService.isLoggedIn(principal)) {
            return "redirect:/login";
        } else {
            return "redirect:/projects";
        }
    }
}