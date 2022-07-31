package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupRepositorySettings;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.GroupRepositorySettingsService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GroupSettingsController {
    private static final String SETTINGS_PAGE = "groupSettings";
    private static final String GROUP_REPOSITORY = "elements/groupRepository";

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    @Autowired
    private GroupRepositorySettingsService groupRepositorySettingsService;

    @Autowired
    private GroupsController groupsController;

    /**
     * Get mapping to fetch group settings page
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The  group settings html page
     */
    @GetMapping("/groupSettings-{id}")
    public String groups(@AuthenticationPrincipal AuthState principal, Model model, @PathVariable String id){
        int userId = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(userId);
        int groupId = Integer.parseInt(id);
        GroupDetailsResponse response = groupsClientService.getGroupDetailsById(groupId);
        if (response.getGroupId() == 0) {
            return "redirect:/groups";
        }
        Group group = new Group(response);
        model.addAttribute("group", group);
        model.addAttribute("userInGroup", groupsClientService.userInGroup(group.getGroupId(), userId));
        model.addAttribute("user", user);
        return SETTINGS_PAGE;
    }

    /**
     * Get mapping to fetch an updated copy of the group repository information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param id The group id
     * @return A html fragment that contains the updated repository information
     */
    @GetMapping("/groupSettings-{id}-repository")
    public String groupRepository(Model model, @PathVariable String id) {
        GroupRepositorySettings groupRepositorySettings = groupRepositorySettingsService.getGroupRepositoryByGroupId(Integer.parseInt(id));
        model.addAttribute("groupRepositorySettings", groupRepositorySettings);
        return GROUP_REPOSITORY;
    }

    /**
     * A post mapping to update the given groups repository
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param repositoryName The new repository name
     * @param gitlabAccessToken The new repository api key
     * @param gitlabProjectId The new repository id
     * @param gitlabServerUrl The new repository server URL
     * @param id The group id
     * @return A html fragment that contains the updated repository information
     */
    @PostMapping("/groupSettings-{id}-repository")
    public String updateGroupRepository(@AuthenticationPrincipal AuthState principal,
                                        Model model,
                                        @RequestParam("repositoryName") String repositoryName,
                                        @RequestParam("gitlabAccessToken") String gitlabAccessToken,
                                        @RequestParam("gitlabProjectId") String gitlabProjectId,
                                        @RequestParam("gitlabServerUrl") String gitlabServerUrl,
                                        @PathVariable String id) {
        // Update the group repository information
        int groupId = Integer.parseInt(id);
        groupRepositorySettingsService.updateRepositoryInformation(groupId, repositoryName, gitlabAccessToken, gitlabProjectId, gitlabServerUrl);

        // Return the updated repository information
        GroupRepositorySettings groupRepositorySettings = groupRepositorySettingsService.getGroupRepositoryByGroupId(Integer.parseInt(id));
        model.addAttribute("groupRepositorySettings", groupRepositorySettings);
        return GROUP_REPOSITORY;
    }


}
