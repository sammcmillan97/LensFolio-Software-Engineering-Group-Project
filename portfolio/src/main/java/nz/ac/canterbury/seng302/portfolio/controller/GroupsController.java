package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupListResponse;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class GroupsController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    private static final String GROUPS_PAGE = "groups";

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
        groups.add(getTeacherGroup());
        groups.add(getGrouplessGroup());
        model.addAttribute("groups", groups);
        return GROUPS_PAGE;
    }

    /**
     * Create groupless group by removing users that are in a group
     * @return groupless group
     */
    protected Group getGrouplessGroup(){
        GroupListResponse groupListResponse = groupsClientService.getAllGroups();
        List<Group> groups = groupListResponse.getGroups();
        groups.add(getTeacherGroup());

        Set<User> allUsers = getAllUsers();
        Set<User> groupless = new HashSet<>();
        boolean userIsInGroup;
        for(User userInAllUsers: allUsers){
            userIsInGroup = false;
            for (Group group: groups){
                for (User userInGroup: group.getMembers()){
                    if(userInAllUsers.getId()==userInGroup.getId()){
                        userIsInGroup = true;
                    }
                }
            }
            if (!userIsInGroup){
                groupless.add(userInAllUsers);
            }
        }
        return new Group(-1, "Groupless", "Members without a group", 0, groupless);
    }

    /**
     * Create teacher group from user roles
     * @return teacher group
     */
    protected Group getTeacherGroup(){
        Set<User> allUsers = getAllUsers();
        Set<User> teachers = new HashSet<>();
        for (User user: allUsers){
            if (isTeacher(user)){
                teachers.add(user);
            }
        }
        return new Group(-2, "Teachers", "Teaching Staff", 0, teachers);
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

            //Handle the pagination aspect of service method
            if (sizeOfSet < 50){
                sizeOfSet = 0;
            } else {
                offset += 50;
            }
        }
        return users;
    }

    protected boolean isTeacher(User user) {
        return user.getRoles().contains(UserRole.TEACHER);
    }

    @RequestMapping(value = "/groups/addMembers", method=RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String saveGroupEdits(@AuthenticationPrincipal AuthState principal,
                                 @RequestParam("groupId") String groupIdString,
                                 @RequestParam(value="members") List<Integer> members,
                                 Model model) {
        System.out.println(members);
        System.out.println(groupIdString);
        //Check if it is a teacher making the request
        if (!userAccountClientService.isTeacher(principal)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to make these changes\n");
        }
        System.out.println(members);
        System.out.println(groupIdString);
        int userId = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(userId);

        return "groupsTable";
    }

}
