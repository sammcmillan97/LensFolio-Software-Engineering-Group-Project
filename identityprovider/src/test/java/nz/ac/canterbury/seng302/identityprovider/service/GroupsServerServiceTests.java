package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class GroupsServerServiceTests {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Spy
    @Autowired
    private GroupsServerService groupServerService;

    @Autowired
    private UserAccountsServerService userService;

    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;
    private static final int testParentProjectId = 1;
    private int testGroupId;
    private Group testGroup;
    private User testUser;

    @BeforeEach
    public void setUp() {
        groupRepository.deleteAll();
        userRepository.deleteAll();
    }



    /**
     * Create group tests
     */

    @Test
    void whenNoGroups_testSaveValidGroup(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                        .setShortName("Short")
                        .setLongName("Looooong")
                        .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertTrue(response.getIsSuccess());
        assertEquals("Successfully created group", response.getMessage());
        groups = groupRepository.findAll();
        assertEquals(1, groups.size());
    }

    @Test
    void whenGroupExistsWithShortName_testSaveGroupWithSameShortName(){
        groupRepository.save(new Group("Short", "LongNameLongName"));
        Set<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("Short")
                .setLongName("Looooong")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Group short name already in use", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(1, groups.size());
    }

    @Test
    void whenGroupExistsWithLongName_testSaveGroupWithSameLongName(){
        groupRepository.save(new Group("Short", "LongNameLongName"));
        Set<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("Small")
                .setLongName("LongNameLongName")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Group long name already in use", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(1, groups.size());
    }

    @Test
    void whenNoGroups_testSaveGroupWithBlankShortName(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("")
                .setLongName("LongNameLongName")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Short name cannot be empty", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

    @Test
    void whenNoGroups_testSaveGroupWithBlankLongName(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("Short")
                .setLongName("")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Long name cannot be empty", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

    @Test
    void whenNoGroups_testSaveGroupWithTooBigShortName(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("a".repeat(SHORT_NAME_MAX_LENGTH + 1))
                .setLongName("LongNameLongName")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Short name must be less than " + SHORT_NAME_MAX_LENGTH + " chars", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

    @Test
    void whenNoGroups_testSaveGroupWithTooBigLongName(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("Short")
                .setLongName("a".repeat(LONG_NAME_MAX_LENGTH + 1))
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Long name must be less than " + LONG_NAME_MAX_LENGTH + "chars", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

    /**
     * Modify Group Details Tests
     */

    @Test
    void whenGroupExists_testEditWithValidDetails(){
        groupRepository.save(new Group("Short", "LongNameLongName"));
        int existingGroupId = groupRepository.findByShortName("Short").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("Shorter")
                .setLongName("Looooonger")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertTrue(response.getIsSuccess());
        assertEquals("Successfully modified group", response.getMessage());
        assertEquals("Shorter", groupRepository.findByGroupId(existingGroupId).getShortName());
        assertEquals("Looooonger", groupRepository.findByGroupId(existingGroupId).getLongName());
    }

    @Test
    void whenGroupExists_testEditGroupWithInvalidId(){
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(1)
                .setShortName("Shorter")
                .setLongName("Looooonger")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Group does not exist", response.getValidationErrors(0).getErrorText());
        assertEquals("groupId", response.getValidationErrors(0).getFieldName());
    }

    @Test
    void whenGroupExistsWithShortName_testEditGroupToSameShortName(){
        groupRepository.save(new Group("Short", "LongNameLongName"));
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("Short")
                .setLongName("BiggerNameBiggerName")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Group short name already in use", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        assertEquals("Little", groupRepository.findByGroupId(existingGroupId).getShortName());
    }

    @Test
    void whenGroupExistsWithLongName_testEditGroupToSameLongName(){
        groupRepository.save(new Group("Short", "LongNameLongName"));
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("Little")
                .setLongName("LongNameLongName")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Group long name already in use", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        assertEquals("BiggerNameBiggerName", groupRepository.findByGroupId(existingGroupId).getLongName());
    }

    @Test
    void whenGroupExists_testEditGroupWithBlankShortName(){
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("")
                .setLongName("BiggerNameBiggerName")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Short name cannot be empty", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        assertEquals("Little", groupRepository.findByGroupId(existingGroupId).getShortName());
    }

    @Test
    void whenGroupExists_testEditGroupWithBlankLongName(){
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("Little")
                .setLongName("")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Long name cannot be empty", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        assertEquals("BiggerNameBiggerName", groupRepository.findByGroupId(existingGroupId).getLongName());
    }

    @Test
    void whenGroupExists_testEditGroupWithTooBigShortName(){
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("a".repeat(SHORT_NAME_MAX_LENGTH + 1))
                .setLongName("BiggerNameBiggerName")
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Short name must be less than " + SHORT_NAME_MAX_LENGTH + " chars", response.getValidationErrors(0).getErrorText());
        assertEquals("shortName", response.getValidationErrors(0).getFieldName());
        assertEquals("Little", groupRepository.findByGroupId(existingGroupId).getShortName());
    }

    @Test
    void whenGroupExists_testEditGroupWithTooBigLongName(){
        groupRepository.save(new Group("Little", "BiggerNameBiggerName"));
        int existingGroupId = groupRepository.findByShortName("Little").getGroupId();
        ModifyGroupDetailsRequest request = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(existingGroupId)
                .setShortName("Little")
                .setLongName("a".repeat(LONG_NAME_MAX_LENGTH + 1))
                .build();
        ModifyGroupDetailsResponse response =  groupServerService.modifyGroupDetailsHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Modify group failed: Validation failed",response.getMessage());
        assertEquals("Long name must be less than " + LONG_NAME_MAX_LENGTH + "chars", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        assertEquals("BiggerNameBiggerName", groupRepository.findByGroupId(existingGroupId).getLongName());
    }

    /**
     *Delete Group Tests
     **/

    @Test
    void whenGroupExists_testDeleteGroup() {
        groupRepository.save(new Group("ShortName", "LongName"));
        int groupId = groupRepository.findByShortName("ShortName").getGroupId();
        Set<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        DeleteGroupResponse response = groupServerService.deleteGroupHandler(request);
        assertTrue(response.getIsSuccess());
        assertEquals("Group deleted successfully", response.getMessage());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

    @Test
    void whenGroupExists_andGroupHasUsers_testDeleteGroupDoesntDeleteUsers() {
        setupForPaginationTests();
        Set<Group> groups = groupRepository.findAll();
        assertEquals(3, groups.size());
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
                .setGroupId(testGroupId)
                .build();
        DeleteGroupResponse response = groupServerService.deleteGroupHandler(request);
        assertTrue(response.getIsSuccess());
        assertEquals("Group deleted successfully", response.getMessage());
        groups = groupRepository.findAll();
        assertEquals(2, groups.size());
        int groupId2 = groupRepository.findByShortName("Group 2").getGroupId();
        int groupId3 = groupRepository.findByShortName("Group 3").getGroupId();
        Group group2 = groupRepository.findByGroupId(groupId2);
        Group group3 = groupRepository.findByGroupId(groupId3);

        assertEquals(2, group2.getMembers().size());
        assertEquals(1, group3.getMembers().size());
    }

    @Test
    void whenNoGroupsExist_testDeleteGroup() {
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());
        DeleteGroupRequest request = DeleteGroupRequest.newBuilder()
                .setGroupId(0)
                .build();
        DeleteGroupResponse response = groupServerService.deleteGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Deleting group failed: Group does not exist", response.getMessage());
    }

    @Test
    void whenGroupExists_getGroupDetails() {
        Group group = new Group("ShortName", "LongName");
        User user = new User("test", "first", "mid", "last", "a",
                "this", "he/him", "test@test.com", "password");
        group.addMember(user);
        groupRepository.save(group);
        int groupId = groupRepository.findByShortName("ShortName").getGroupId();
        Set<Group> groups = groupRepository.findAll();
        assertEquals(1, groups.size());

        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        GroupDetailsResponse response = groupServerService.getGroupDetailsHandler(request);
        List<UserResponse> userResponses = new ArrayList<>();
        for (User member : group.getMembers()) {
            userResponses.add(userService.getUserAccountByIdHandler(member.getUserIdRequest()));
        }
        assertEquals("ShortName", response.getShortName());
        assertEquals("LongName", response.getLongName());
        assertEquals(userResponses, response.getMembersList());
    }

    @Test
    void whenGroupDoesNotExist_getGroupDetails() {
        Set<Group> groups = groupRepository.findAll();
        assertEquals(0, groups.size());

        GetGroupDetailsRequest request = GetGroupDetailsRequest.newBuilder()
                .setGroupId(0)
                .build();
        GroupDetailsResponse response = groupServerService.getGroupDetailsHandler(request);
        assertEquals("", response.getShortName());
        assertEquals("", response.getLongName());
        assertEquals(0, response.getMembersList().size());
    }

    /**
     * This method is to add groups to the repository for certain tests as some require the repository being empty
     */
    void setupForPaginationTests() {
        User testUser1 = new User("testUser1", "Frank", "Frankie", "McFrank", "Frankie", "I am Frank", "he/him", "frank@frank.com", "frank123");
        User testUser2 = new User("testUser2", "Frank2", "Frankie2", "McFrank2", "Frankie2", "I am Frank2", "he/him", "frank2@frank.com", "frank123");
        User testUser3 = new User("testUser3", "Frank3", "Frankie3", "McFrank3", "Frankie3", "I am Frank3", "he/him", "frank3@frank.com", "frank123");
        Group testGroup1 = new Group("Group 1", "cTest group 1 long name", testParentProjectId);
        Group testGroup2 = new Group("Group 2", "aTest group 2 long name", testParentProjectId);
        Group testGroup3 = new Group("Group 3", "bTest group 3 long name", testParentProjectId);
        testGroup1.addMember(testUser1);
        testGroup1.addMember(testUser2);
        testGroup1.addMember(testUser3);
        testGroup2.addMember(testUser1);
        testGroup2.addMember(testUser2);
        testGroup3.addMember(testUser1);
        groupRepository.save(testGroup1);
        groupRepository.save(testGroup2);
        groupRepository.save(testGroup3);

        testGroupId = testGroup1.getGroupId();
    }

    // Test that getting the group by id fails if the group doesn't exist
    @Test
    void getGroupByIdWhenGroupDoesntExist() {
        GetGroupDetailsRequest getGroupDetailsRequest = GetGroupDetailsRequest.newBuilder().setGroupId(-1).build();
        GroupDetailsResponse response = groupServerService.getGroupByIdHandler(getGroupDetailsRequest);
        assertEquals("", response.getLongName());
        assertEquals("", response.getShortName());
        assertEquals(new ArrayList<>(), response.getMembersList());
    }

    // Test that getting the group by id succeeds if the group exists
    @Test
    void getGroupByIdWhenGroupExists() {
        setupForPaginationTests();
        GetGroupDetailsRequest getGroupDetailsRequest = GetGroupDetailsRequest.newBuilder().setGroupId(testGroupId).build();
        GroupDetailsResponse response = groupServerService.getGroupByIdHandler(getGroupDetailsRequest);
        assertEquals("cTest group 1 long name", response.getLongName());
        assertEquals("Group 1", response.getShortName());
        assertEquals(3, response.getMembersCount());
    }

    // Provides arguments for the parameterized tests for paginated groups
    static Stream<Arguments> paginatedGroupsTestParamProvider() {
        return Stream.of(
                // All tests have a list of 3 groups
                arguments(1, 9999, 2), // Tests that the offset of 1 returns a list of 2 groups
                arguments(0, 0, 0), // Tests that a limit of 0 returns 0 groups
                arguments(0, 1, 1), // Tests that a limit of 1 returns 1 group
                arguments(3, 9999, 0) // Tests that an offset higher than the number of groups returns 0 groups
        );
    }

    // Tests that the offset and limit options for pagination work as expected. See above method for test cases
    @ParameterizedTest
    @MethodSource("paginatedGroupsTestParamProvider")
    void getPaginatedGroupsTest(int offset, int limit, int expectedGroupListSize) {
        setupForPaginationTests();
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy("short")
                .setIsAscendingOrder(true)
                .build();
        PaginatedGroupsResponse response = groupServerService.getPaginatedGroupsHandler(getPaginatedGroupsRequest);
        assertEquals(expectedGroupListSize, response.getGroupsList().size());
    }

    // Tests that sort by group short name works correctly
    @Test
    void getPaginatedGroupsSortByShortName() {
        setupForPaginationTests();
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("short")
                .setIsAscendingOrder(true)
                .build();
        PaginatedGroupsResponse response = groupServerService.getPaginatedGroupsHandler(getPaginatedGroupsRequest);
        assertEquals("Group 1", response.getGroupsList().get(0).getShortName());
        assertEquals("Group 2", response.getGroupsList().get(1).getShortName());
        assertEquals("Group 3", response.getGroupsList().get(2).getShortName());
    }

    // Tests that sort by group long name works correctly
    @Test
    void getPaginatedGroupsSortByLongName() {
        setupForPaginationTests();
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("long")
                .setIsAscendingOrder(true)
                .build();
        PaginatedGroupsResponse response = groupServerService.getPaginatedGroupsHandler(getPaginatedGroupsRequest);
        assertEquals("Group 2", response.getGroupsList().get(0).getShortName());
        assertEquals("Group 3", response.getGroupsList().get(1).getShortName());
        assertEquals("Group 1", response.getGroupsList().get(2).getShortName());
    }

    // Tests that sort by group short name works correctly
    @Test
    void getPaginatedGroupsSortByNumMembers() {
        setupForPaginationTests();
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("members")
                .setIsAscendingOrder(true)
                .build();
        PaginatedGroupsResponse response = groupServerService.getPaginatedGroupsHandler(getPaginatedGroupsRequest);
        assertEquals("Group 3", response.getGroupsList().get(0).getShortName());
        assertEquals("Group 2", response.getGroupsList().get(1).getShortName());
        assertEquals("Group 1", response.getGroupsList().get(2).getShortName());
    }

    // Tests that sort by group short name works correctly
    @Test
    void getPaginatedGroupsSortByNumMembersDescending() {
        setupForPaginationTests();
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(0)
                .setLimit(9999)
                .setOrderBy("members")
                .setIsAscendingOrder(false)
                .build();
        PaginatedGroupsResponse response = groupServerService.getPaginatedGroupsHandler(getPaginatedGroupsRequest);
        assertEquals("Group 1", response.getGroupsList().get(0).getShortName());
        assertEquals("Group 2", response.getGroupsList().get(1).getShortName());
        assertEquals("Group 3", response.getGroupsList().get(2).getShortName());
    }

    /**
     * Add user tests
     */

    private void setUpForAddingRemovingMembers() {
        testGroup = groupRepository.save(new Group( "ShortName", "LongName"));
        testUser = userRepository.save(new User("Tester1", "First", "Middle",
                "Last", "Nick", "Bio", "Test, Tester", "test@email.com", "password"));
    }

    @Test
    void whenAGroupExists_addOneUser() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        User user = userRepository.findByUserId(testUser.getUserId());
        userIdsToBeAdded.add(user.getUserId());
        assertEquals(0, testGroup.getMembers().size());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);
        testGroup = groupRepository.findByShortName("ShortName");
        testUser = userRepository.findByUsername("Tester1");

        assertEquals("User(s) added successfully", addGroupMembersResponse.getMessage());
        assertEquals(1, testGroup.getMembers().size());
        assertTrue(testGroup.getMembers().contains(testUser));
    }

    @Test
    void whenAGroupExists_addTwoDifferentUsers() {
        User testUser2 = userRepository.save(new User("Tester2", "First", "Middle",
                "Last", "Nick", "Bio", "Test, Tester", "test@email.com", "password"));
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        User user = userRepository.findByUserId(testUser.getUserId());
        userIdsToBeAdded.add(user.getUserId());
        userIdsToBeAdded.add(testUser2.getUserId());
        assertEquals(0, testGroup.getMembers().size());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        testGroup = groupRepository.findByShortName("ShortName");
        assertEquals("User(s) added successfully", addGroupMembersResponse.getMessage());
        assertEquals(2, testGroup.getMembers().size());
    }

    @Test
    void whenAGroupExists_addTwoOfTheSameUser() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        User user = userRepository.findByUserId(testUser.getUserId());
        userIdsToBeAdded.add(user.getUserId());
        userIdsToBeAdded.add(user.getUserId());
        assertEquals(0, testGroup.getMembers().size());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        testGroup = groupRepository.findByShortName("ShortName");
        assertFalse(addGroupMembersResponse.getIsSuccess());
        assertEquals(0, testGroup.getMembers().size());
    }

    @Test
    void whenAGroupExists_addAUserThatDoestNotExist(){
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        userIdsToBeAdded.add(-1);
        assertEquals(0, testGroup.getMembers().size());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        testGroup = groupRepository.findByShortName("ShortName");
        assertEquals("Add group members failed: User -1 does not exist", addGroupMembersResponse.getMessage());
        assertEquals(0, testGroup.getMembers().size());
        assertFalse(addGroupMembersResponse.getIsSuccess());
    }

    @Test
    void addAUserToAGroupThatDoesNotExist() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        userIdsToBeAdded.add(testUser.getUserId());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(-1)
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        assertEquals("Add group members failed: Group -1 does not exist", addGroupMembersResponse.getMessage());
        assertFalse(addGroupMembersResponse.getIsSuccess());
    }

    @Test
    void whenAGroupExists_AddAUserThatExistsAndOneThatDoesnt() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        userIdsToBeAdded.add(testUser.getUserId());
        userIdsToBeAdded.add(-1);
        assertEquals(0, testGroup.getMembers().size());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        assertFalse(addGroupMembersResponse.getIsSuccess());
        assertEquals("Add group members failed: User -1 does not exist", addGroupMembersResponse.getMessage());
        assertEquals(0, testGroup.getMembers().size());
    }

    @Test
    void whenAGroupExistsWithAUser_ReAddThatUser() {
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        groupRepository.save(testGroup);
        assertEquals(1, testGroup.getMembers().size());
        List<Integer> userIdsToBeAdded = new ArrayList<>();
        userIdsToBeAdded.add(testUser.getUserId());

        AddGroupMembersRequest userRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeAdded)
                .build();
        AddGroupMembersResponse addGroupMembersResponse = groupServerService.addGroupMembersHandler(userRequest);

        assertFalse(addGroupMembersResponse.getIsSuccess());
        assertEquals(1, testGroup.getMembers().size());
    }

    @Test
    void whenAGroupExistsWithAUser_RemoveThatUser() {
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        assertEquals(1, testGroup.getMembers().size());
        groupRepository.save(testGroup);
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(testUser.getUserId());

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);

        testGroup = groupRepository.findByGroupId(testGroup.getGroupId());
        assertEquals(0, testGroup.getMembers().size());
        assertTrue(removeGroupMembersResponse.getIsSuccess());
        assertEquals("User(s) removed successfully", removeGroupMembersResponse.getMessage());
    }

    @Test
    void whenAGroupExists_AddUser_ThenEditUser_ThenRemoveUser() {
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        groupRepository.save(testGroup);
        testUser.setFirstName("Newname");
        userRepository.save(testUser);
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(testUser.getUserId());

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);
        testGroup = groupRepository.findByGroupId(testGroup.getGroupId());
        assertEquals(0, testGroup.getMembers().size());
        assertTrue(removeGroupMembersResponse.getIsSuccess());
        assertEquals("User(s) removed successfully", removeGroupMembersResponse.getMessage());
    }

    @Test
    void whenAGroupExistsWithAUser_ThenRemoveUserThatDoesNotExist() {
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        assertEquals(1, testGroup.getMembers().size());
        groupRepository.save(testGroup);
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(-1);

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);

        testGroup = groupRepository.findByGroupId(testGroup.getGroupId());
        assertEquals(1, testGroup.getMembers().size());
        assertFalse(removeGroupMembersResponse.getIsSuccess());
        assertEquals("Remove group members failed: User -1 does not exist", removeGroupMembersResponse.getMessage() );
    }

    @Test
    void RemoveUserFromAGroupThatDoesNotExist() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(testUser.getUserId());

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(-1)
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);

        assertFalse(removeGroupMembersResponse.getIsSuccess());
        assertEquals("Remove group members failed: Group -1 does not exist", removeGroupMembersResponse.getMessage());
    }

    @Test
    void whenAGroupExistsWithAUser_RemoveUserThatExistsAndOneThatDoesnt() {
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        groupRepository.save(testGroup);
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(testUser.getUserId());
        userIdsToBeRemoved.add(-1);
        assertEquals(1, testGroup.getMembers().size());

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);

        assertFalse(removeGroupMembersResponse.getIsSuccess());
        assertEquals("Remove group members failed: User -1 does not exist", removeGroupMembersResponse.getMessage());
        assertEquals(1, testGroup.getMembers().size());
    }

    @Test
    void whenAGroupExistsWithNoUsers_RemoveAUser() {
        setUpForAddingRemovingMembers();
        List<Integer> userIdsToBeRemoved = new ArrayList<>();
        userIdsToBeRemoved.add(testUser.getUserId());
        userIdsToBeRemoved.add(-1);

        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(testGroup.getGroupId())
                .addAllUserIds(userIdsToBeRemoved)
                .build();
        RemoveGroupMembersResponse removeGroupMembersResponse = groupServerService.removeGroupMembersHandler(removeGroupMembersRequest);
        assertFalse(removeGroupMembersResponse.getIsSuccess());
        assertEquals(0, testGroup.getMembers().size());
    }

    @Test
    void whenUserInGroup_testUserInGroup(){
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        groupRepository.save(testGroup);
        assertTrue(groupServerService.userInGroup(testGroup.getGroupId(), testUser.getUserId()));
    }

    @Test
    void whenUserNotInGroup_testUserInGroup(){
        setUpForAddingRemovingMembers();
        testGroup.addMember(testUser);
        User testUser2 = new User("testUser2", "Frank2", "Frankie2", "McFrank2", "Frankie2", "I am Frank2", "he/him", "frank2@frank.com", "frank123");
        groupRepository.save(testGroup);
        userRepository.save(testUser2);
        assertFalse(groupServerService.userInGroup(testGroup.getGroupId(), testUser2.getUserId()));
    }
}
