package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Set;

import static nz.ac.canterbury.seng302.shared.identityprovider.UserRole.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupServerServiceTests {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Spy
    @Autowired
    private GroupServerService groupServerService;


    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;
    private final String testUsername = "test user";
    private final String testFirstName = "test fname";
    private final String testMiddleName = "test mname";
    private final String testLastName = "test lname";
    private final String testNickname = "test nname";
    private final String testBio = "test bio";
    private final String testPronouns = "test/tester";
    private final String testEmail = "test@email.com";
    private final String testPassword = "test password";

    private final UserRole studentRole = STUDENT;
    private final UserRole teacherRole = TEACHER;
    private final UserRole adminRole = COURSE_ADMINISTRATOR;

    private int testId;
    private Timestamp testCreated;

    @BeforeEach
    public void setUp() {
        groupRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.deleteAll();
        User testUser = userRepository.save(new User(testUsername, testFirstName, testMiddleName, testLastName, testNickname, testBio, testPronouns, testEmail, testPassword));
        testId = testUser.getUserId();
        testCreated = testUser.getTimeCreated();
    }


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
                .setShortName("a".repeat(33))
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
                .setLongName("a".repeat(129))
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        assertFalse(response.getIsSuccess());
        assertEquals("Create group failed: Validation failed",response.getMessage());
        assertEquals("Long name must be less than " + LONG_NAME_MAX_LENGTH + "chars", response.getValidationErrors(0).getErrorText());
        assertEquals("longName", response.getValidationErrors(0).getFieldName());
        groups = groupRepository.findAll();
        assertEquals(0, groups.size());
    }

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
    void whenAGroupExists_addOneUser() {
        Iterable<Integer> usersIdsToBeRemoved = userRepository.f


        CreateGroupRequest request = CreateGroupRequest.newBuilder()
                .setShortName("Short")
                .setLongName("Looooong")
                .build();
        CreateGroupResponse response =  groupServerService.createGroupHandler(request);
        AddGroupMembersRequest request AddGroupMembersRequest.newBuilder()
                .setGroupId(0)
                .setUserIds()
    }

}
