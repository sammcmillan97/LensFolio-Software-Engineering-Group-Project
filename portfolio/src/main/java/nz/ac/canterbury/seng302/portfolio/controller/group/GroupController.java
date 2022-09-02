package nz.ac.canterbury.seng302.portfolio.controller.group;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.group.GroupRepositorySettings;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.group.GitlabConnectionService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupRepositorySettingsService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class GroupController {
    private static final String GROUP_PAGE = "templatesGroup/group";
    private static final String GROUP_REPOSITORY = "fragmentsGroup/groupRepositorySettings";

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    @Autowired
    private GroupRepositorySettingsService groupRepositorySettingsService;
    @Autowired
    private GitlabConnectionService gitlabConnectionService;

    /**
     * Get mapping to fetch group settings page
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The  group settings html page
     */
    @GetMapping("/group-{id}")
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
        return GROUP_PAGE;
    }

    /**
     * Get mapping to fetch an updated copy of the group repository information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @param id The group id
     * @return A html fragment that contains the updated repository information
     */
    @GetMapping("/group-{id}-repository")
    public String groupRepository(Model model, @PathVariable String id, @RequestParam("firstLoad") boolean firstLoad) {
        GroupRepositorySettings groupRepositorySettings = groupRepositorySettingsService.getGroupRepositorySettingsByGroupId(Integer.parseInt(id));

        List<Commit> commits = null;
        try {
            commits = gitlabConnectionService.getAllCommits(Integer.parseInt(id));
        } catch (Exception ignored) {
            // Ignored because the commits variable is already null.
        }
        model.addAttribute("firstLoad", firstLoad);
        model.addAttribute("changesSaved", false);
        model.addAttribute("commits", commits);
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
    @PostMapping("/group-{id}-repository")
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
        GroupRepositorySettings groupRepositorySettings = groupRepositorySettingsService.getGroupRepositorySettingsByGroupId(groupId);
        List<Commit> commits = null;
        try {
            commits = gitlabConnectionService.getAllCommits(groupId);
        } catch (GitLabApiException ignored) {
            // Ignored because the commits variable is already null.
        } catch (NoSuchFieldException ignored) {
            // Ignored because this only occurs if the group doesn't have any repository settings, but
            // we've just added them, so it must have settings.
        }
        model.addAttribute("firstLoad", false);
        model.addAttribute("changesSaved", true);
        model.addAttribute("commits", commits);
        model.addAttribute("groupRepositorySettings", groupRepositorySettings);
        return GROUP_REPOSITORY;
    }


}
