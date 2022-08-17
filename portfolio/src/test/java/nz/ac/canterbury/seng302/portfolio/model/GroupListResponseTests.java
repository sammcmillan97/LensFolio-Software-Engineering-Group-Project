package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.group.GroupListResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroupListResponseTests {
    // Tests that creating a GroupListResponse from a PaginatedGroupResponse carries all the information over properly.
    @Test
    void givenValidDetails_testGroupDetailsResponse() {

        UserResponse userResponse = UserResponse.newBuilder().setFirstName("test").build();
        UserResponse userResponse2 = UserResponse.newBuilder().setFirstName("test2").build();
        GroupDetailsResponse groupDetailsResponse = GroupDetailsResponse.newBuilder()
                .setShortName("group")
                .setLongName("longgroupname")
                .addAllMembers(Arrays.asList(userResponse, userResponse2))
                .setGroupId(1)
                .build();

        PaginatedGroupsResponse source = PaginatedGroupsResponse.newBuilder().addGroups(groupDetailsResponse).build();
        GroupListResponse response = new GroupListResponse(source);
        assertEquals(source.getResultSetSize(), response.getResultSetSize());
        Group expectedGroup = new Group(groupDetailsResponse);
        assertEquals(expectedGroup, response.getGroups().get(0));
    }
}
