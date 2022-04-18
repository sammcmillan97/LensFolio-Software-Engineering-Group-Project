package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserAccountClientService {

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub userStub;

    @GrpcClient("identity-provider-grpc-server")
    private UserAccountServiceGrpc.UserAccountServiceStub userNonBlockingStub;

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

    public Iterable<User> getPaginatedUsers(int offset, int limit, String orderBy) {
        GetPaginatedUsersRequest getPaginatedUsersRequest = GetPaginatedUsersRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy(orderBy)
                .build();
        PaginatedUsersResponse response = userStub.getPaginatedUsers(getPaginatedUsersRequest);
        ArrayList<User> users = new ArrayList<>();
        for(UserResponse userResponse: response.getUsersList()) {
            User user = new User(userResponse);
            users.add(user);
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
        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {

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

    public String getRole(AuthState principal) {
        return principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
    }

}
