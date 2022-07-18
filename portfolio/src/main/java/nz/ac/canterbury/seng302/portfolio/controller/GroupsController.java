package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.GroupListResponse;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AddGroupMembersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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

    private static final int GROUPLESS_GROUP_ID = -1;
    private static final int TEACHER_GROUP_ID = -2;

    /**
     * Get mapping to fetch groups page
     * @param principal Authentication principal storing current user information
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The  groups html page
     */
    @GetMapping("/groups")
    public String groups(@AuthenticationPrincipal AuthState principal, Model model){
        int id = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(id);
        boolean userIsTeacher = userAccountClientService.isTeacher(principal);
        boolean userIsAdmin = userAccountClientService.isAdmin(principal);

        GroupListResponse groupListResponse = groupsClientService.getAllGroups();
        List<Group> groups = groupListResponse.getGroups();
        groups.add(getTeacherGroup());
        groups.add(getGrouplessGroup());
        model.addAttribute("groups", groups);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userIsTeacher);
        model.addAttribute("userIsAdmin", userIsAdmin);
        model.addAttribute("GROUPLESS_GROUP_ID", GROUPLESS_GROUP_ID);
        model.addAttribute("TEACHER_GROUP_ID", TEACHER_GROUP_ID);
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
        List<User> groupless = new ArrayList<>();
        boolean userIsInGroup;
        for(User userInAllUsers: allUsers){
            userIsInGroup = false;
            for (Group group: groups){
                for (User userInGroup: group.getMembers()){
                    if (userInAllUsers.getId() == userInGroup.getId()) {
                        userIsInGroup = true;
                        break;
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
        List<User> teachers = new ArrayList<>();
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

    /**
     * Checks if the given user is in the given group
     * @param userId User id of the user to check
     * @param groupId Group id of the group to check
     * @return A boolean, true if the user is in the group, false otherwise
     */
    private boolean userInGroup(int userId, int groupId) {
        Group group = new Group(groupsClientService.getGroupDetailsById(groupId));
        for (User member : group.getMembers()) {
            if (member.getId() == userId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a group id and a list of members, adds those members to the group,
     * then returns an updated group table as HTML
     * @param principal Authentication principal storing current user information
     * @param groupId The group id to add members to
     * @param members A list of member ids to be added to the group
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return An updated group table
     */
    @PostMapping("/group-{groupId}-members")
    @ResponseStatus(HttpStatus.OK)
    public String addMembers(@AuthenticationPrincipal AuthState principal,
                                 @PathVariable("groupId") Integer groupId,
                                 @RequestParam(value="members") List<Integer> members,
                                 Model model) {

        int id = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(id);
        boolean userIsTeacher = userAccountClientService.isTeacher(principal);
        boolean userIsAdmin = userAccountClientService.isAdmin(principal);

        Group group = new Group();
        //Check if it is a teacher making the request
        if (!userIsTeacher) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to make these changes\n");
        } else if (groupId == -2) { // Add to teacher group
            // Give the users the teacher role
            for (int member : members) {
                // Only add the role if user isn't already a teacher
                if (!userAccountClientService.getUserAccountById(member).getRoles().contains(UserRole.TEACHER)) {
                    userAccountClientService.addRole(member, UserRole.TEACHER);
                }
            }
            group = getTeacherGroup();
        } else if (groupId != -1) { // If not adding to groupless group
            // Figure out what users to add to the group
            List<Integer> usersToAdd = new ArrayList<>();
            for (int userId : members) {
                // Only add the user if they aren't already in the group
                if (!userInGroup(userId, groupId)) {
                    usersToAdd.add(userId);
                }
            }

            // Add the users to the group and fetch an updated group object
            if (!usersToAdd.isEmpty()) {
                groupsClientService.addGroupMembers(groupId, usersToAdd);
            }
            group = new Group(groupsClientService.getGroupDetailsById(groupId));
        }

        model.addAttribute("group", group);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userIsTeacher);
        model.addAttribute("userIsAdmin", userIsAdmin);
        model.addAttribute("GROUPLESS_GROUP_ID", GROUPLESS_GROUP_ID);
        model.addAttribute("TEACHER_GROUP_ID", TEACHER_GROUP_ID);

        return "groupTable";
    }

    /**
     * Takes a groupId and returns a group table as HTML
     * Used to update the group tables on the groups page
     * @param principal Authentication principal storing current user information
     * @param groupId The group id to fetch
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return An updated group table
     */
    @GetMapping("group-{groupId}-members")
    public String getMembers(@AuthenticationPrincipal AuthState principal,
                                @PathVariable("groupId") Integer groupId,
                                Model model) {

        int userId = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(userId);
        boolean userIsTeacher = userAccountClientService.isTeacher(principal);
        boolean userIsAdmin = userAccountClientService.isAdmin(principal);

        Group group;

        if (groupId == -2) { // teacher group
            group = getTeacherGroup();
        } else if (groupId == -1) { // groupless group
            group = getGrouplessGroup();
        } else {
            group = new Group(groupsClientService.getGroupDetailsById(groupId));
        }
        model.addAttribute("group", group);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userIsTeacher);
        model.addAttribute("userIsAdmin", userIsAdmin);
        model.addAttribute("GROUPLESS_GROUP_ID", GROUPLESS_GROUP_ID);
        model.addAttribute("TEACHER_GROUP_ID", TEACHER_GROUP_ID);
        return "groupTable";
    }

    /**
     * Takes a group id and a list of members, removes those members from the group,
     * then returns an updated group table as HTML
     * @param principal Authentication principal storing current user information
     * @param groupId The group id to remove members from
     * @param members A list of member ids to be removed from the group
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return An updated group table
     */
    @DeleteMapping("/group-{groupId}-members")
    @ResponseStatus(HttpStatus.OK)
    public String removeMembers(@AuthenticationPrincipal AuthState principal,
                                 @PathVariable("groupId") Integer groupId,
                                 @RequestParam(value="members") List<Integer> members,
                                 Model model) {

        int id = userAccountClientService.getUserId(principal);
        User user = userAccountClientService.getUserAccountById(id);
        boolean userIsTeacher = userAccountClientService.isTeacher(principal);
        boolean userIsAdmin = userAccountClientService.isAdmin(principal);

        Group group = new Group();
        //Check if it is a teacher making the request
        if (!userIsTeacher) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not authorized to make these changes\n");
        } else if (groupId == -2) { // Remove from teacher group
            for (int member : members) {
                // Only remove the role if user is a teacher
                if (userAccountClientService.getUserAccountById(member).getRoles().contains(UserRole.TEACHER)) {
                    userAccountClientService.removeRole(member, UserRole.TEACHER);
                }
            }
            group = getTeacherGroup();
        } else if (groupId != -1) { // Not the groupless group
            // Figure out what users to remove from the group
            List<Integer> usersToRemove = new ArrayList<>();
            for (int userId : members) {
                // Only remove the user if they are in the group
                if (userInGroup(userId, groupId)) {
                    usersToRemove.add(userId);
                }
            }

            // Remove the users from the group and fetch an updated group object
            if (!usersToRemove.isEmpty()) {
                groupsClientService.removeGroupMembers(groupId, usersToRemove);
            }
            group = new Group(groupsClientService.getGroupDetailsById(groupId));
        }

        model.addAttribute("group", group);
        model.addAttribute("user", user);
        model.addAttribute("userIsTeacher", userIsTeacher);
        model.addAttribute("userIsAdmin", userIsAdmin);
        model.addAttribute("GROUPLESS_GROUP_ID", GROUPLESS_GROUP_ID);
        model.addAttribute("TEACHER_GROUP_ID", TEACHER_GROUP_ID);

        return "groupTable";
    }

}
