package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioGroupService;
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
    private PortfolioGroupService portfolioGroupService;

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

    @GetMapping("/groupSettings-{id}-repository")
    public String groupRepository(Model model, @PathVariable String id) {
        PortfolioGroup portfolioGroup = portfolioGroupService.getGroupById(Integer.parseInt(id));
        model.addAttribute("portfolioGroup", portfolioGroup);
        return GROUP_REPOSITORY;
    }

    @PostMapping("/groupSettings-{id}-repository")
    public String updateGroupRepository(@AuthenticationPrincipal AuthState principal,
                                        Model model,
                                        @RequestParam("repositoryName") String repositoryName,
                                        @RequestParam("repositoryApiKey") String repositoryApiKey,
                                        @RequestParam("repositoryId") String repositoryId,
                                        @RequestParam("repositoryServerUrl") String repositoryServerUrl,
                                        @PathVariable String id) {
        int groupId = Integer.parseInt(id);
        portfolioGroupService.updateRepositoryInformation(groupId, repositoryName, repositoryApiKey, Integer.parseInt(repositoryId), repositoryServerUrl);

        PortfolioGroup portfolioGroup = portfolioGroupService.getGroupById(Integer.parseInt(id));
        model.addAttribute("portfolioGroup", portfolioGroup);
        return GROUP_REPOSITORY;
    }


}
