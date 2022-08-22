package nz.ac.canterbury.seng302.portfolio.service.user;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.model.user.UserListResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class UserAccountClientService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userStub;

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userNonBlockingStub;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    public static List<byte[]> divideArray(byte[] source, int chunksize) {

        List<byte[]> result = new ArrayList<>();
        int start = 0;
        while (start < source.length) {
            int end = Math.min(source.length, start + chunksize);
            result.add(Arrays.copyOfRange(source, start, end));
            start += chunksize;
        }
        return result;
    }

    /**
     * Creates a request to be sent to the IDP for requesting a paginated list of user responses
     * @param offset The number of users to be sliced from the original list of users from the DB
     * @param limit The max number of users to be returned to the list
     * @param orderBy How the list of users will be sorted: "name", "username", "alias" and "roles"
     * @param isAscending Whether the list is ascending or descending
     * @return A list of paginated, sorted and ordered user responses
     */
    public UserListResponse getPaginatedUsers(int offset, int limit, String orderBy, boolean isAscending) {
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy(orderBy)
                .setIsAscendingOrder(isAscending)
                .build();
        PaginatedUsersResponse response = userStub.getPaginatedUsers(getPaginatedUsersRequest);
        return new UserListResponse(response);
    }

    /**
     * Uses a dummy request to fetch the number of users in the database, then performs a
     * request to fetch all of the users in the database.
     * Results are sorted by userId in ascending order.
     * @return A list of paginated, sorted and ordered user responses
     */
    public UserListResponse getAllUsers() {
        // Set variables for sort order and offset/limit
        int offset = 0;
        int limit = 0;
        String orderBy = "userId";
        boolean isAscending = true;

        // Send a request to find out how many users are in the database
        GetPaginatedUsersRequest infoPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy(orderBy)
                .setIsAscendingOrder(isAscending)
                .build();

        PaginatedUsersResponse tempResponse = userStub.getPaginatedUsers(infoPaginatedUsersRequest);

        // Make the limit the number of users in the database and then fetch all users.
        limit = tempResponse.getResultSetSize();
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy(orderBy)
                .setIsAscendingOrder(isAscending)
                .build();
        PaginatedUsersResponse response = userStub.getPaginatedUsers(getPaginatedUsersRequest);
        return new UserListResponse(response);
    }

    /**
     * Fetches all users from the idp then filters them to a list of users without the given id.
     * Used for autofill on the create evidence page because you shouldn't be able to add yourself to a piece of evidence
     * @param userId The user id that isn't wanted
     * @return a list of all users with a different user id to the one given
     */
    public List<User> getAllUsersExcept(int userId) {
        UserListResponse userListResponse = getAllUsers();
        List<User> users = new ArrayList<>();
        for (User user : userListResponse.getUsers()) {
            if (userId != user.getId()) {
                users.add(user);
            }
        }
        return users;
    }

    /**
     * Allows for uploading photos to the IDP. The URL will be stored in the IDP for retrieval.
     * @param fileContent The photo content, in bytes
     * @param userId The ID of the user
     * @param fileType The file type of the photo
     */
    public void uploadUserProfilePhoto(byte[] fileContent, int userId, String fileType){
        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<>() {

            @Override
            public void onNext(FileUploadStatusResponse response) { // This is where to put the client's implementation after server sends a message
                if (response.getStatus() == FileUploadStatus.FAILED) {
                    onError(new IllegalStateException());
                }
                onError(new IllegalStateException());
            }

            @Override
            public void onCompleted() {
                // Client should close connection, so this completion is never utilised
            }

            @Override
            public void onError(Throwable throwable) {
                // Default implementation should handle this case.
            }

        };

        StreamObserver<UploadUserProfilePhotoRequest> requestObserver = userNonBlockingStub.uploadUserProfilePhoto(responseObserver);
        ProfilePhotoUploadMetadata metaData = ProfilePhotoUploadMetadata.newBuilder()
                .setUserId(userId)
                .setFileType(fileType)
                .build();
        UploadUserProfilePhotoRequest request = UploadUserProfilePhotoRequest.newBuilder().setMetaData(metaData).build();
        requestObserver.onNext(request);
        List<byte[]> byteArrays = divideArray(fileContent, 65536);
        for (byte[] byteArray: byteArrays) {
            UploadUserProfilePhotoRequest uploadRequest = UploadUserProfilePhotoRequest.newBuilder().setFileContent(ByteString.copyFrom(byteArray)).build();
            requestObserver.onNext(uploadRequest);
        }
        requestObserver.onCompleted();
    }

    public DeleteUserProfilePhotoResponse deleteUserProfilePhoto(final int userId)  {
        DeleteUserProfilePhotoRequest request = DeleteUserProfilePhotoRequest.newBuilder().setUserId(userId).build();
        return userStub.deleteUserProfilePhoto(request);
    }

    public ChangePasswordResponse changeUserPassword(final int userId, final String currentPassword, final String newPassword)  {
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.newBuilder()
                .setUserId(userId)
                .setCurrentPassword(currentPassword)
                .setNewPassword(newPassword)
                .build();
        return userStub.changeUserPassword(changePasswordRequest);
    }


    public EditUserResponse editUser(final int userId, final String firstName, final String middleName,
                                     final String lastName, final String nickname, final String bio,
                                     final String personalPronouns, final String email)  {
        EditUserRequest editUserRequest = EditUserRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        return userStub.editUser(editUserRequest);
    }

    public User getUserAccountById(final int userId)  {
        GetUserByIdRequest getUserByIdRequest = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();
        UserResponse response = userStub.getUserAccountById(getUserByIdRequest);
        return new User(response);
    }

    /**
     * Uses the AuthState of the logged in user to get their user object
     * @param principal Authentication principal storing current user information
     * @return the logged in user's User object
     */
    public User getUserAccountByPrincipal(AuthState principal){
        int userId = getUserId(principal);
        return getUserAccountById(userId);
    }

    /**
     * Uses the AuthState of the logged in user to get their user id
     * @param principal Authentication principal storing current user information
     * @return the logged in user's id
     */
    public int getUserId(AuthState principal) {
        return Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
    }

    public UserRegisterResponse register(final String username, final String password, final String firstName,
                                         final String middleName, final String lastName, final String nickname,
                                         final String bio, final String personalPronouns, final String email)  {
        UserRegisterRequest userRegisterRequest = UserRegisterRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .setFirstName(firstName)
                .setMiddleName(middleName)
                .setLastName(lastName)
                .setNickname(nickname)
                .setBio(bio)
                .setPersonalPronouns(personalPronouns)
                .setEmail(email)
                .build();
        return userStub.register(userRegisterRequest);
    }

    protected Collection<UserRole> getRoles(AuthState principal) {
        return getUserAccountByPrincipal(principal).getRoles();
    }

    public boolean isLoggedIn(AuthState principal) {
        return principal != null;
    }

    /**
     * Add role to a user
     * @param userId of user being altered
     * @param role being added
     * @return response
     */
    public UserRoleChangeResponse addRole(int userId, UserRole role) {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(role)
                .build();
        return userStub.addRoleToUser(modifyRoleOfUserRequest);
    }

    /**
     * Remove role from a user
     * @param userId of user being altered
     * @param role being removed
     * @return response
     */
    public UserRoleChangeResponse removeRole(int userId, UserRole role) {
        ModifyRoleOfUserRequest modifyRoleOfUserRequest = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(role)
                .build();
        return userStub.removeRoleFromUser(modifyRoleOfUserRequest);
    }

    public boolean isTeacher(AuthState principal) {
        return isTeacherHandler(getRoles(principal));
    }

    public boolean isTeacherHandler(Collection<UserRole> roles){
        return roles.contains(UserRole.TEACHER) || roles.contains(UserRole.COURSE_ADMINISTRATOR);
    }

    public boolean isAdmin(AuthState principal) {
        return isAdminHandler(getRoles(principal));
    }

    protected boolean isAdminHandler(Collection<UserRole> roles){
        return roles.contains(UserRole.COURSE_ADMINISTRATOR);
    }

    /**
     * Only for mocking purposes
     * Updates the current stub with a new one
     * @param newStub the new (mocked) GroupsServiceBlockingStub
     */
    @VisibleForTesting
    protected void setUserStub(UserAccountServiceGrpc.UserAccountServiceBlockingStub newStub) {
        userStub = newStub;
    }

    /**
     * Only for mocking purposes
     * @return the current groups stub
     */
    @VisibleForTesting
    protected UserAccountServiceGrpc.UserAccountServiceBlockingStub getUserStub() {
        return userStub;
    }

}
