package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupTests {

    // Test that groups are equal with all properties
    @Test
    void givenTwoGroupsHaveEqualProperties_testGroupsAreEqual() {
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
    void givenShortNamesDifferent_testGroupsUnequal() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setShortName("Goup").build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setShortName("Group").build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the groups are equal when their short names match
    @Test
    void givenShortNamesSame_testGroupsEqual() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setShortName("Goup").build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their long names don't match
    @Test
    void givenLongNamesDifferent_testGroupsUnequal() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setLongName("Goup").build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setLongName("Group").build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the groups are equal when their long names match
    @Test
    void givenLongNamesSame_testGroupsEqual() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setLongName("Goup").build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their ids don't match
    @Test
    void givenIdsDifferent_testGroupsUnequal() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setGroupId(1).build();
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().setGroupId(2).build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the groups are equal when their ids match
    @Test
    void givenIdsSame_testGroupsEqual() {
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().setGroupId(1).build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

    // Test that the groups aren't equal when their user lists don't match
    @Test
    void givenUsersDifferent_testGroupsUnequal() {
        List<UserResponse> members = new ArrayList<>();
        members.add(UserResponse.newBuilder().setFirstName("Frank").build());
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        members.add(UserResponse.newBuilder().setLastName("Frankie").build());
        GroupDetailsResponse response2 = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        Group group = new Group(response);
        Group group2 = new Group(response2);
        assertNotEquals(group, group2);
    }

    // Test that the groups are equal when their user lists match
    @Test
    void givenUsersSame_testGroupsEqual() {
        List<UserResponse> members = new ArrayList<>();
        members.add(UserResponse.newBuilder().setFirstName("Frank").build());
        members.add(UserResponse.newBuilder().setLastName("Frankie").build());
        GroupDetailsResponse response = GroupDetailsResponse.newBuilder().addAllMembers(members).build();
        Group group = new Group(response);
        Group group2 = new Group(response);
        assertEquals(group, group2);
    }

}


