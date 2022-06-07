package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyGroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GroupsServerServiceTests {

    @Autowired
    private GroupRepository groupRepository;

    @Spy
    @Autowired
    private GroupServerService groupServerService;

    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;

    @BeforeEach
    public void setUp() {
        groupRepository.deleteAll();
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
        assertEquals("BiggerNameBiggerName", groupRepository.findByGroupId(existingGroupId).getLongName());;
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

}
