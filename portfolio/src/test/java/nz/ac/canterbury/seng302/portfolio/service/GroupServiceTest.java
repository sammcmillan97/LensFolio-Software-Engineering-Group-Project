package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.Group;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GroupServiceTest {

    @Spy
    @Autowired
    GroupsClientService groupsClientService;

    // Test that a user is in a group when they are in it
    @Test
    void whenUserInGroup_testUserIdInGroup() {
        UserResponse userResponse = UserResponse.newBuilder()
                .setUsername("abc")
                        .setFirstName("123")
                        .setMiddleName("123")
                        .setLastName("123")
                        .setNickname("123")
                        .setBio("123")
                        .setPersonalPronouns("12/3")
                        .setEmail("123@asd.as")
                        .setCreated(Timestamp.newBuilder().build())
                        .setId(1)
                        .addAllRoles(new HashSet<>())
        .build();
        User testUser = new User(userResponse);
        GroupDetailsResponse groupResponse = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .setLongName("Group")
                .setShortName("group")
                .addMembers(userResponse).build();
        Group testGroup = new Group(groupResponse);
        int testGroupId = testGroup.getGroupId();
        int testUserId = testUser.getId();
        GroupsClientService spyGroupService = Mockito.spy(groupsClientService);
        Mockito.doReturn(groupResponse).when(spyGroupService).getGroupDetailsById(testGroupId);
        assertTrue(spyGroupService.userInGroup(testGroupId, testUserId));
    }

    // Test that a user is in a group when they are not in it
    @Test
    void whenUserNotInGroup_testUserIdInGroup() {
        List<UserResponse> users = new ArrayList<>();
        GroupDetailsResponse groupResponse = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .setLongName("Group")
                .setShortName("group")
                .addAllMembers(users).build();
        Group testGroup = new Group(groupResponse);
        int testGroupId = testGroup.getGroupId();
        int testUserId = 1;
        GroupsClientService spyGroupService = Mockito.spy(groupsClientService);
        Mockito.doReturn(groupResponse).when(spyGroupService).getGroupDetailsById(testGroupId);
        assertFalse(spyGroupService.userInGroup(testGroupId, testUserId));
    }
}
