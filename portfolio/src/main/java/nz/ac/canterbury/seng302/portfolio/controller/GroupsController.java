package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupListResponse;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class GroupsController {
    @Autowired
    private UserAccountClientService userAccountClientService;
    @Autowired
    private GroupsClientService groupsClientService;

    /**
     * Get mapping to fetch groups page
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The user or teacher groups html page depending on the logged in user
     */
    @GetMapping("/groups")
    public String groups(@AuthenticationPrincipal AuthState principal, Model model){
        int id = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userAccountClientService.isTeacher(principal));
        GroupListResponse groupListResponse = groupsClientService.getAllGroups();
        List<Group> groups = groupListResponse.getGroups();
        groups.add(getGrouplessGroup());
        groups.add(getTeacherGroup());
        model.addAttribute("groups", groups);
        System.out.println(groupListResponse.getGroups().get(1).getMembers());
        return "groups";
    }

    protected Group getGrouplessGroup(){
        Set<User> allUsers = getAllUsers();

        return new Group(-1, "Groupless", "Members without a group", 0, allUsers);
    }

    protected Group getTeacherGroup(){
        Set<User> allUsers = getAllUsers();

        return new Group(-2, "Teaching staff", "Members with role teacher", 0, allUsers);
    }

    /**
     * Retrieve all users
     * @return set of all Users
     */
    protected Set<User> getAllUsers() {
        Set<User> users = new HashSet<>();
        int offset = 0;
        int sizeOfSet = 1;

        while (sizeOfSet > 0) {
            UserListResponse userListResponse = userAccountClientService.getPaginatedUsers(offset, 50, "userId", true);
            List<User> returnedUsers = userListResponse.getUsers();
            sizeOfSet = userListResponse.getResultSetSize();

            if (sizeOfSet > 0){
                users.addAll(returnedUsers);
            }
            offset += 50;

            if (sizeOfSet < 50){
                sizeOfSet = 0;
            }
        }
        return users;
    }
}
