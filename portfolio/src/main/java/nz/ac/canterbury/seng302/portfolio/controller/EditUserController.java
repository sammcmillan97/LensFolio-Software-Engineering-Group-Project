package nz.ac.canterbury.seng302.portfolio.controller;


import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Controller
public class EditUserController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Get mapping to open editUser page
     * @param principal
     * @param model
     * @return the editUser page
     */
    @GetMapping("/editUser")
    public String editUser(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        return "editUser";
    }


    /**
     * Edit user post mapping,
     * called when user submits an edit user request
     * @param principal
     * @param email (updated)
     * @param firstName (updated)
     * @param middleName (updated)
     * @param lastName (updated)
     * @param nickname (updated)
     * @param pronouns (updated)
     * @param bio (updated)
     * @param model (updated)
     * @return editUser page if unsuccessful, or profile page if successful
     */
    @PostMapping("/editUser")
    public String editUser(@AuthenticationPrincipal AuthState principal,
                           @RequestParam(name="username") String username,
                           @RequestParam(name="email") String email,
                           @RequestParam(name="firstName") String firstName,
                           @RequestParam(name="middleName") String middleName,
                           @RequestParam(name="lastName") String lastName,
                           @RequestParam(name="nickname") String nickname,
                           @RequestParam(name="pronouns") String pronouns,
                           @RequestParam(name="bio") String bio,
                           Model model) {

        //get userId using the Authentication Principle
        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        //should add validation to ensure that other a user can only edit themselves (or possibly include admin priveldges

        EditUserResponse editUserResponse;

        //some validation, could use more, same as register
        if (email.isBlank() || firstName.isBlank() || lastName.isBlank()){
            //due to form resetting, you need to get the existing user again
            UserResponse user = userAccountClientService.getUserAccountById(id);
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Oops! Please make sure that spaces are not used in required fields");
            return "editUser";
        }

        try {
            //Call the edit user via grpc with users validated params
            editUserResponse = userAccountClientService.editUser(id, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response", editUserResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "editUser";
        }

        //Get the new version of user
        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);

        //if edit user was successful
        if (editUserResponse.getIsSuccess()){
            //generic model attribut that need to be set for the profile page
            model.addAttribute("name", user.getFirstName() + " " + user.getLastName());
            Timestamp ts = user.getCreated();
            Instant timeCreated = Instant.ofEpochSecond( ts.getSeconds() , ts.getNanos() );
            LocalDate dateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
            long months = ChronoUnit.MONTHS.between(dateCreated, LocalDate.now());
            String formattedDate = "Member Since: " + dateCreated + " (" + months + " months)";
            model.addAttribute("date", formattedDate);
            return "profile";
        } else {
            //if edit user was unsuccessful
            model.addAttribute("editMessage", "");
            model.addAttribute("editMessage", editUserResponse.getMessage());
            return "/editUser";
        }
    }

}