package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.group.GroupListResponse;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.model.user.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.user.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class GroupsController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private GroupsClientService groupsClientService;

    private static final String GROUPS_PAGE = "groups";

    private static final String GROUP_STRING = "group";
    private static final String GROUPS_STRING = "groups";

    private static final String GROUPS_TABLE = "elements/groupTable";

    private static final String USER_IS_MEMBER = "userIsMember";

    private static final int GROUPLESS_GROUP_ID = -1;
    private static final String GROUPLESS_GROUP_ID_STRING = "GROUPLESS_GROUP_ID";

    private static final int TEACHER_GROUP_ID = -2;
    private static final String TEACHER_GROUP_ID_STRING = "TEACHER_GROUP_ID";

    private static final String USER_IS_TEACHER = "userIsTeacher";
    private static final String USER_IS_ADMIN = "userIsAdmin";



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
        List<Integer> allGroupIds = new ArrayList<>();
        for (Group g : groups) {
            allGroupIds.add(g.getGroupId());
        }

        model.addAttribute(GROUPS_STRING, groups);
        model.addAttribute("allGroupIds", allGroupIds);
        model.addAttribute("user", user);
        model.addAttribute(USER_IS_TEACHER, userIsTeacher);
        model.addAttribute(USER_IS_ADMIN, userIsAdmin);
        model.addAttribute(GROUPLESS_GROUP_ID_STRING, GROUPLESS_GROUP_ID);
        model.addAttribute(TEACHER_GROUP_ID_STRING, TEACHER_GROUP_ID);
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
        return new Group(GROUPLESS_GROUP_ID, "Groupless", "Members without a group", 0, groupless);
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
        return new Group(TEACHER_GROUP_ID, "Teachers", "Teaching Staff", 0, teachers);
    }

    /**
     * Retrieve all users
     * @return set of all Users
     */
    protected Set<User> getAllUsers() {
        Set<User> users = new HashSet<>();
        int offset = 0;
        boolean endOfUsersReached = false;

        while (!endOfUsersReached) {
            UserListResponse userListResponse = userAccountClientService.getPaginatedUsers(offset, 500, "userId", true);
            List<User> returnedUsers = userListResponse.getUsers();

            if (!returnedUsers.isEmpty()){
                users.addAll(returnedUsers);
                offset += 500;
            } else {
                endOfUsersReached = true;
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
    protected boolean userInGroup(int userId, int groupId) {
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

        Group group;
        //Check if it is a teacher making the request
        if (!userIsTeacher) {
            if (groupId == GROUPLESS_GROUP_ID){
                group = getGrouplessGroup();
            } else if (groupId == TEACHER_GROUP_ID) {
                group = getTeacherGroup();
            } else {
                group = new Group(groupsClientService.getGroupDetailsById(groupId));
            }
            model.addAttribute(GROUP_STRING, group);
        } else if (groupId == TEACHER_GROUP_ID) { // Add to teacher group
            // Give the users the teacher role
            for (int member : members) {
                // Only add the role if user isn't already a teacher
                if (!userAccountClientService.getUserAccountById(member).getRoles().contains(UserRole.TEACHER)) {
                    userAccountClientService.addRole(member, UserRole.TEACHER);
                }
            }
            group = getTeacherGroup();
        } else if (groupId == GROUPLESS_GROUP_ID) {

            // Remove each user from all their groups
            for (int memberId : members) {
                for (Group tempGroup : groupsClientService.getAllGroups().getGroups()) {
                    if (userInGroup(memberId, tempGroup.getGroupId())) {
                        groupsClientService.removeGroupMembers(tempGroup.getGroupId(), List.of(memberId));
                    }
                }
                if (userAccountClientService.getUserAccountById(memberId).getRoles().contains(UserRole.TEACHER)) {
                    userAccountClientService.removeRole(memberId, UserRole.TEACHER);
                }
            }
            group = getGrouplessGroup();

        } else { // Adding to a regular group
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

        model.addAttribute(GROUP_STRING, group);
        model.addAttribute("user", user);
        model.addAttribute(USER_IS_TEACHER, userIsTeacher);
        model.addAttribute(USER_IS_ADMIN, userIsAdmin);
        model.addAttribute(GROUPLESS_GROUP_ID_STRING, GROUPLESS_GROUP_ID);
        model.addAttribute(TEACHER_GROUP_ID_STRING, TEACHER_GROUP_ID);

        return GROUPS_TABLE;
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

        if (groupId == TEACHER_GROUP_ID) { // teacher group
            group = getTeacherGroup();
        } else if (groupId == GROUPLESS_GROUP_ID) { // groupless group
            group = getGrouplessGroup();
        } else {
            group = new Group(groupsClientService.getGroupDetailsById(groupId));
        }
        model.addAttribute(GROUP_STRING, group);
        model.addAttribute("user", user);
        model.addAttribute(USER_IS_TEACHER, userIsTeacher);
        model.addAttribute(USER_IS_ADMIN, userIsAdmin);
        model.addAttribute(USER_IS_MEMBER, userInGroup(userId, groupId));
        model.addAttribute(GROUPLESS_GROUP_ID_STRING, GROUPLESS_GROUP_ID);
        model.addAttribute(TEACHER_GROUP_ID_STRING, TEACHER_GROUP_ID);
        return GROUPS_TABLE;
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
            if (groupId == GROUPLESS_GROUP_ID){
                group = getGrouplessGroup();
            } else if (groupId == TEACHER_GROUP_ID) {
                group = getTeacherGroup();
            } else {
                group = new Group(groupsClientService.getGroupDetailsById(groupId));
            }
            model.addAttribute(GROUP_STRING, group);
        } else if (groupId == TEACHER_GROUP_ID) { // Remove from teacher group
            for (int member : members) {
                // Only remove the role if user is a teacher
                if (userAccountClientService.getUserAccountById(member).getRoles().contains(UserRole.TEACHER)) {
                    userAccountClientService.removeRole(member, UserRole.TEACHER);
                }
            }
            group = getTeacherGroup();
        } else if (groupId != GROUPLESS_GROUP_ID) { // Not the groupless group
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

        model.addAttribute(GROUP_STRING, group);
        model.addAttribute("user", user);
        model.addAttribute(USER_IS_TEACHER, userIsTeacher);
        model.addAttribute(USER_IS_ADMIN, userIsAdmin);
        model.addAttribute(GROUPLESS_GROUP_ID_STRING, GROUPLESS_GROUP_ID);
        model.addAttribute(TEACHER_GROUP_ID_STRING, TEACHER_GROUP_ID);

        return GROUPS_TABLE;
    }

}
