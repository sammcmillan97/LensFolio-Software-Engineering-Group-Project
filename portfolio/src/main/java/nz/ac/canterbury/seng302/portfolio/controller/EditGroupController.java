package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.DeleteGroupResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EditGroupController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    private static final String GROUPS_REDIRECT = "redirect:/groups";

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
