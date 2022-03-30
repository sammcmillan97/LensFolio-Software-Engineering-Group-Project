package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteUserProfilePhotoResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Controller
public class ProfilePictureController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Get mapping to open addProfilePicture page
     * @param principal
     * @param model
     * @return the addProfilePicture page
     */
    @GetMapping("/addProfilePicture")
    public String addProfilePicture(
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
        model.addAttribute("username", user.getUsername());
        return "addProfilePicture";
    }



    @PostMapping("/addProfilePicture")
    public String addProfilePicture(@AuthenticationPrincipal AuthState principal,
                                    @RequestParam(name="fileContent") String base64FileContent,
                                    @RequestParam(name="fileType") String fileType,
                                    Model model) {

        if (fileType == null) {
            model.addAttribute("errorMessage", "Please select a profile before saving changes");
            return "/addProfilePicture";
        }

        //get userId using the Authentication Principle
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] decodedByte = decoder.decode(base64FileContent.split(",")[1]);
            userAccountClientService.uploadUserProfilePhoto(decodedByte, id, fileType);
            // Generic attributes that need to be set for the profile page
            UserResponse user = userAccountClientService.getUserAccountById(id);
            model.addAttribute("user", user);
            model.addAttribute("name", user.getFirstName() + " " + user.getLastName());
            Timestamp ts = user.getCreated();
            Instant timeCreated = Instant.ofEpochSecond( ts.getSeconds() , ts.getNanos() );
            LocalDate dateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
            long months = ChronoUnit.MONTHS.between(dateCreated, LocalDate.now());
            String formattedDate = "Member Since: " + dateCreated + " (" + months + " months)";
            model.addAttribute("date", formattedDate);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e);
        }

        return "addProfilePicture";
    }

    @PostMapping("/removeProfilePicture")
    public String removeProfilePicture(@AuthenticationPrincipal AuthState principal,
                                       @RequestParam(name="username") String username,
                                       Model model) {

        //get userId using the Authentication Principle
        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        DeleteUserProfilePhotoResponse deleteUserProfilePhotoResponse ;
        deleteUserProfilePhotoResponse = userAccountClientService.deleteUserProfilePhoto(id);

        //Get the new version of user
        UserResponse user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        //if remove profile picture was successful return to profile
        if (deleteUserProfilePhotoResponse.getIsSuccess()){
            //generic model attribute that need to be set for the profile page
            model.addAttribute("name", user.getFirstName() + " " + user.getLastName());
            Timestamp ts = user.getCreated();
            Instant timeCreated = Instant.ofEpochSecond( ts.getSeconds() , ts.getNanos() );
            LocalDate dateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
            long months = ChronoUnit.MONTHS.between(dateCreated, LocalDate.now());
            String formattedDate = "Member Since: " + dateCreated + " (" + months + " months)";
            model.addAttribute("date", formattedDate);
            model.addAttribute("successMessage", "Your profile picture has been successfully been removed");
            return "redirect:/profile";
        } else {
            //if edit user was unsuccessful
            model.addAttribute("deleteMessage", "");
            model.addAttribute("deleteMessage", deleteUserProfilePhotoResponse.getMessage());
            return "/addProfilePicture";
        }
    }

}
