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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Controller
public class ProfileController {

    @Autowired
    private UserAccountClientService userService;

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
        model.addAttribute("date", getTimeSinceCreated(user.getCreated()));
        return "profile";
    }

    /**
     * Method to create a string representing time since member was created
     * @param ts Timestamp representing the user attribute timeSinceCreated
     * @return formattedDate a string representing time since account was created to be displayed in profile
     */
    private String getTimeSinceCreated(Timestamp ts) {
        Instant timeCreated = Instant.ofEpochSecond( ts.getSeconds() , ts.getNanos() );
        LocalDate localDateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
        Date dateCreated = java.util.Date.from(localDateCreated.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String formattedDate = "Member Since: " + dateFormat.format(dateCreated) + " ";
        long totalMonths = ChronoUnit.MONTHS.between(localDateCreated, LocalDate.now());

        long months = totalMonths % 12;
        long years = Math.floorDiv(totalMonths, 12);

        formattedDate += "(";
        if (years > 0) {
            String yearPlural = " years";
            if (years == 1) {
                yearPlural = " year";
            }
            formattedDate += years + yearPlural + " ";
        }

        String monthPlural = " months";
        if(months == 1) {
            monthPlural = " month";
        }
        formattedDate += months + monthPlural + ")";

        return formattedDate;
    }
}

