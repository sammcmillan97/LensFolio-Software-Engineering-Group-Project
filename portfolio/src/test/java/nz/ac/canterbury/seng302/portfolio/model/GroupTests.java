package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class GroupTests {

    // Test that groups are equal with all properties
    @Test
    void testGroupsEqualAllProperties() {
        List<UserResponse> members = new ArrayList<>();
        members.add(UserResponse.newBuilder().setFirstName("Frank").build());
        members.add(UserResponse.newBuilder().setLastName("Frankie").build());
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder()
                .setGroupId(1)
                .setShortName("Franks Group")
                .setLongName("Group for Frank")
                .addAllMembers(members)
                .build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their short names don't match
    @Test
    void testUsersUnequalWhenShortNamesDifferent() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setShortName("Goup").build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setShortName("Group").build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the users are equal when their short names match
    @Test
    void testUsersEqualWhenShortNamesSame() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setShortName("Goup").build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their long names don't match
    @Test
    void testUsersUnequalWhenLongNamesDifferent() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setLongName("Goup").build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setLongName("Group").build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the users are equal when their long names match
    @Test
    void testUsersEqualWhenLongNamesSame() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setLongName("Goup").build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their ids don't match
    @Test
    void testUsersUnequalWhenIdsDifferent() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setGroupId(1).build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setGroupId(2).build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the users are equal when their ids match
    @Test
    void testUsersEqualWhenIdsSame() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setGroupId(1).build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their ids don't match
    @Test
    void testUsersUnequalWhenUsersDifferent() {
        List<UserResponse> members = new ArrayList<>();
        members.add(UserResponse.newBuilder().setFirstName("Frank").build());
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        members.add(UserResponse.newBuilder().setLastName("Frankie").build());
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the users are equal when their ids match
    @Test
    void testUsersEqualWhenUsersSame() {
        List<UserResponse> members = new ArrayList<>();
        members.add(UserResponse.newBuilder().setFirstName("Frank").build());
        members.add(UserResponse.newBuilder().setLastName("Frankie").build());
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

}


