package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Controller
public class ProfileController {

    @Autowired
    private UserAccountClientService userService;

    /**
     * Display the user's profile page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The string "profile"
     */
    @GetMapping("/profile")
    public String profile(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        UserResponse user = userService.getUserAccountById(id);
        model.addAttribute("user", user);
        model.addAttribute("name", user.getFirstName() + " " + user.getLastName());
        Timestamp ts = user.getCreated();
        Instant timeCreated = Instant.ofEpochSecond( ts.getSeconds() , ts.getNanos() );
        LocalDate dateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
        long months = ChronoUnit.MONTHS.between(dateCreated, LocalDate.now());
        String formattedDate = "Member Since: " + dateCreated + " (" + months + " months)";
        model.addAttribute("date", formattedDate);
        return "profile";
    }


}

