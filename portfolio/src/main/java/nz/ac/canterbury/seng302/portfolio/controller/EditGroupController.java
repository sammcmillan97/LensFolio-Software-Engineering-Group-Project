package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Event;
import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditGroupController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    private static final String GROUPS_REDIRECT = "redirect:/groups";

    /**
     * The get mapping to return the page to add/edit an group
     */
    @GetMapping("/editGroup-{groupId}")
    public String editGroup(@AuthenticationPrincipal AuthState principal,
                            @PathVariable("groupId") String groupId,
                            Model model) {

        //Check User is a teacher otherwise return to project page
        if (!userAccountClientService.isTeacher(principal)) {
            return "redirect:/projects";
        }

        // Add user details to model for displaying in top banner
        int userId = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(userId);
        model.addAttribute("user", user);

        Group group;
        //Check if it is existing or new group
        if (Integer.parseInt(groupId) != -1) {
            group = new Group(groupsClientService.getGroupDetailsById(Integer.parseInt(groupId)));
        } else {
            //Create new group
            group = new Group();
            group.setShortName("Short Name");
            group.setLongName("Long Name");
        }

        //Add event details to model
        model.addAttribute("groupShortName", group.getShortName());
        model.addAttribute("groupLongName", group.getLongName());

        return "editGroup";
    }

    @PostMapping("/editGroup-{id}")
    public String projectSave(@PathVariable("id") String groupId) {
        return GROUPS_REDIRECT;
    }

    /**
     * Delete endpoint for groups. Takes id parameter from http request and deletes the corresponding group from
     * the database.
     * @param groupId ID of the project to be deleted from the database.
     * @return Redirects back to the GET mapping for /groups.
     */
    @DeleteMapping(value="/editGroup-{id}")
    public String deleteGroupById(@AuthenticationPrincipal AuthState principal, @PathVariable("id") String groupId) {
        if (userAccountClientService.isTeacher(principal)) {
            int id = Integer.parseInt(groupId);
            try {
                groupsClientService.deleteGroupById(id);
            } catch (Exception ignored) {
                // Don't do anything if delete fails
            }
        }
        return GROUPS_REDIRECT;
    }
}
