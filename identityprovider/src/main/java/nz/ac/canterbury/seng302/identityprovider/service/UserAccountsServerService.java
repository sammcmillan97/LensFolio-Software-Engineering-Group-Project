package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserAccountsServerService extends UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        ChangePasswordResponse reply = changeUserPasswordHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Handler for password change requests.
     * If the user exists, and their password is correct, change it to the new password.
     * @param request A password change request according to user_accounts.proto
     * @return A password change response according to user_accounts.proto
     */
    @VisibleForTesting
    ChangePasswordResponse changeUserPasswordHandler(ChangePasswordRequest request) {
        ChangePasswordResponse.Builder reply = ChangePasswordResponse.newBuilder();

        if (repository.existsById(request.getUserId())) {
            User user = repository.findByUserId(request.getUserId());
            if (Boolean.TRUE.equals(user.checkPassword(request.getCurrentPassword()))) {
                user.setPassword(request.getNewPassword());
                repository.save(user);
                reply.setIsSuccess(true).setMessage("Successfully changed password");
            } else {
                reply.setIsSuccess(false).setMessage("Password change failed: current password is incorrect");
            }
        } else {
            reply.setIsSuccess(false).setMessage("Password change failed: user does not exist");
        }

        return reply.build();
    }

    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        EditUserResponse reply = editUserHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Handler for edit user requests.
     * If the user exists, change their details to the new details in the request.
     * @param request A user edit request according to user_accounts.proto
     * @return A user edit response according to user_accounts.proto
     */
    @VisibleForTesting
    EditUserResponse editUserHandler(EditUserRequest request) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();

        if (repository.existsById(request.getUserId())) {
            User user = repository.findByUserId(request.getUserId());
            user.setFirstName(request.getFirstName());
            user.setMiddleName(request.getMiddleName());
            user.setLastName(request.getLastName());
            user.setNickname(request.getNickname());
            user.setBio(request.getBio());
            user.setPersonalPronouns(request.getPersonalPronouns());
            user.setEmail(request.getEmail());
            repository.save(user);
            reply.setIsSuccess(true).setMessage("Edit user succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Edit user failed: user does not exist");
        }
        return reply.build();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse reply = getUserAccountByIdHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Handler for user information retrieval requests.
     * If the user id exists, return their information.
     * Else, return blank information.
     * @param request A get user by id request according to user_accounts.proto
     * @return A user response according to user_accounts.proto
     */
    @VisibleForTesting
    UserResponse getUserAccountByIdHandler(GetUserByIdRequest request) {
        UserResponse.Builder reply = UserResponse.newBuilder();

        if (repository.existsById(request.getId())) {
            User user = repository.findByUserId(request.getId());
            reply.setUsername(user.getUsername())
                    .setFirstName(user.getFirstName())
                    .setMiddleName(user.getMiddleName())
                    .setLastName(user.getLastName())
                    .setNickname(user.getNickname())
                    .setBio(user.getBio())
                    .setPersonalPronouns(user.getPersonalPronouns())
                    .setEmail(user.getEmail())
                    .setCreated(user.getTimeCreated());
        }
        return reply.build();
    }

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {

        UserRegisterResponse reply = registerHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();

    }

    /**
     * Handler for user register requests.
     * If the username is already taken, fails.
     * Else, creates the user and returns the new user id.
     * @param request A user register request according to user_accounts.proto
     * @return A user register response according to user_accounts.proto
     */
    @VisibleForTesting
    UserRegisterResponse registerHandler(UserRegisterRequest request) {
        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        String username = request.getUsername();
        String firstName = request.getFirstName();
        String middleName = request.getMiddleName();
        String lastName = request.getLastName();
        String nickname = request.getNickname();
        String bio = request.getBio();
        String personalPronouns = request.getPersonalPronouns();
        String email = request.getEmail();
        String password = request.getPassword();

        if (repository.findByUsername(request.getUsername()) != null) {
            int validationErrorIndex = reply.getValidationErrorsCount();
            ValidationError validationError;
            reply.setValidationErrors();
        }

        if (repository.findByUsername(request.getUsername()) == null) { //Middle name

            repository.save(new User(
                    username,
                    firstName,
                    middleName,
                    lastName,
                    nickname,
                    bio,
                    personalPronouns,
                    email,
                    password));
            reply
                    .setIsSuccess(true)
                    .setNewUserId(repository.findByUsername(request.getUsername()).getUserId())
                    .setMessage("Register attempt succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Register attempt failed: Username already taken");
        }
        return reply.build();
    }

}
