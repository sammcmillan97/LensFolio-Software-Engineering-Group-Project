package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreeterController {

    @Autowired
    private GreeterClientService greeterClientService;

    @GetMapping("/greeting")
    public String greeting(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="name", required=false, defaultValue="Blue") String favouriteColour,
            Model model
    ) {
        // Talk to the GreeterService on the IdP to get a message, we'll tell them our favourite colour too
        String idpMessage = greeterClientService.receiveGreeting(favouriteColour);
        model.addAttribute("idpMessage", idpMessage);

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        // Generate our own message, based on the information we have available to us
        String portfolioMessage = String.format(
                "The portfolio service (which is serving you this message) knows you are logged in as '%s' (role='%s'), with ID=%d",
                principal.getName(),
                role,
                id
            );
        model.addAttribute("portfolioMessage", portfolioMessage);

        // Also pass on just the favourite colour value on its own to use
        model.addAttribute("currentFavouriteColour", favouriteColour);

        return "greeting";
    }

    @PostMapping("/favouriteColour")
    public String favouriteColour(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="favouriteColour") String favouriteColour,
            Model model
    ) {
        return "redirect:/greeting?name=" + favouriteColour;
    }
}
