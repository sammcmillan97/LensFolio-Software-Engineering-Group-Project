package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.util.ArrayList;

/**
 * This class is returned from the method getPaginatedUsers in UserAccountClientService.
 * It is needed because the provided class there is PaginatedUsersResponse, which contains UserResponses.
 * The rest of the application uses User, not UserResponse, so we need to convert from one to the other.
 * That is what this class is for.
 */
public class UserListResponse {

    ArrayList<User> users;
    int resultSetSize;

    /**
     * Create a list of users based on a PaginatedUsersResponse from the identity provider.
     * They contain the same data, but the UserListResponse has a list of Users not a list of UserResponses.
     * @param source The PaginatedUsersResponse to create a list of users from.
     */
    public UserListResponse(PaginatedUsersResponse source) {
        users = new ArrayList<>();
        for(UserResponse userResponse: source.getUsersList()) {
            User user = new User(userResponse);
            users.add(user);
        }
        resultSetSize = source.getResultSetSize();
    }

    public Iterable<User> getUsers() {
        return users;
    }

    public int getResultSetSize() {
        return resultSetSize;
    }

}
