package nz.ac.canterbury.seng302.portfolio.controller.user;


import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * The controller for handling get and post request on the edit user page to edit a user
 */
@Controller
public class EditUserController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Get mapping to open editUser page
     * @param principal Authentication principal storing current user information
     * @param model ThymeLeaf model
     * @return the editUser page
     */
    @GetMapping("/editUser")
    public String editUser(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {

        User user = userAccountClientService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        return "userTemplates/editUser";
    }


    /**
     * Edit user post mapping,
     * called when user submits an edit user request
     * @param principal Authentication principal storing current user information
     * @param email (updated)
     * @param firstName (updated)
     * @param middleName (updated)
     * @param lastName (updated)
     * @param nickname (updated)
     * @param pronouns (updated)
     * @param bio (updated)
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return editUser page if unsuccessful, or profile page if successful
     */
    @PostMapping("/editUser")
    public String editUser(@AuthenticationPrincipal AuthState principal,
                           @RequestParam(name="email") String email,
                           @RequestParam(name="firstName") String firstName,
                           @RequestParam(name="middleName") String middleName,
                           @RequestParam(name="lastName") String lastName,
                           @RequestParam(name="nickname") String nickname,
                           @RequestParam(name="pronouns") String pronouns,
                           @RequestParam(name="bio") String bio,
                           Model model) {

        //get userId using the Authentication Principle
        int id = userAccountClientService.getUserId(principal);

        //should add validation to ensure that other a user can only edit themselves (or possibly include admin privileges

        EditUserResponse editUserResponse;

        try {
            //Call the edit user via grpc with users validated params
            editUserResponse = userAccountClientService.editUser(id, firstName,
                    middleName, lastName, nickname, bio, pronouns, email);
            model.addAttribute("Response", editUserResponse.getMessage());

        } catch (Exception e){
            model.addAttribute("errorMessage", e);
            return "userTemplates/editUser";
        }

        //Get the new version of user
        User user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);

        //if edit user was successful
        if (editUserResponse.getIsSuccess()){
            return "redirect:/profile";
        } else {
            //if edit user was unsuccessful
            model.addAttribute("editedEmail", email);
            model.addAttribute("editedFirstName", firstName);
            model.addAttribute("editedMiddleName", middleName);
            model.addAttribute("editedLastName", lastName);
            model.addAttribute("editedNickname", nickname);
            model.addAttribute("editedPronouns", pronouns);
            model.addAttribute("editedBio", bio);
            model.addAttribute("validationErrors", editUserResponse.getValidationErrorsList());
            return "/editUser";
        }
    }
}