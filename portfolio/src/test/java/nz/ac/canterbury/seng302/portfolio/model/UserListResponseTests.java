package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.model.user.UserListResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserListResponseTests {

    // Tests that creating a UserListResponse from a PaginatedUserResponse carries all the information over properly.
    @Test
    void givenValidDetails_testCreateUserListResponse() {
        UserResponse userResponse = UserResponse.newBuilder().setFirstName("test").build();
        PaginatedUsersResponse source = PaginatedUsersResponse.newBuilder().addUsers(userResponse).build();
        UserListResponse response = new UserListResponse(source);
        assertEquals(source.getResultSetSize(), response.getResultSetSize());
        assertEquals(new User(userResponse), response.getUsers().get(0));
    }

}
