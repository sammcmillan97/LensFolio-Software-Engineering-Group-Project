package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
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
     * Checks if the fields for user creation are valid, and creates the user.
     * If some fields are not valid, instead fails and returns a list of non-valid fields.
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

        if (repository.findByUsername(username) != null) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username already taken").setFieldName("username").build();
            reply.addValidationErrors(validationError);
        }

        if (username.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username is required").setFieldName("username").build();
            reply.addValidationErrors(validationError);
        }

        if (username.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username must be less than 65 characters").setFieldName("username").build();
            reply.addValidationErrors(validationError);
        }

        if (firstName.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name is required").setFieldName("firstName").build();
            reply.addValidationErrors(validationError);
        }

        if (firstName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name must be less than 65 characters").setFieldName("firstName").build();
            reply.addValidationErrors(validationError);
        }

        if (middleName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Middle name must be less than 65 characters").setFieldName("middleName").build();
            reply.addValidationErrors(validationError);
        }

        if (lastName.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name is required").setFieldName("lastName").build();
            reply.addValidationErrors(validationError);
        }

        if (lastName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name must be less than 65 characters").setFieldName("lastName").build();
            reply.addValidationErrors(validationError);
        }

        if (nickname.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Nickname must be less than 65 characters").setFieldName("nickname").build();
            reply.addValidationErrors(validationError);
        }

        if (bio.length() > 1024) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Bio must be less than 1025 characters").setFieldName("bio").build();
            reply.addValidationErrors(validationError);
        }

        if (personalPronouns.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Personal pronouns must be less than 65 characters").setFieldName("personalPronouns").build();
            reply.addValidationErrors(validationError);
        }

        if (email.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email is required").setFieldName("email").build();
            reply.addValidationErrors(validationError);
        } else if (!email.contains("@")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be valid").setFieldName("email").build();
            reply.addValidationErrors(validationError);
        }

        if (email.length() > 255) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be less than 256 characters").setFieldName("email").build();
            reply.addValidationErrors(validationError);
        }

        if (password.length() <= 8) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be at least 8 characters").setFieldName("password").build();
            reply.addValidationErrors(validationError);
        }

        if (password.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be less than 65 characters").setFieldName("password").build();
            reply.addValidationErrors(validationError);
        }

        if (reply.getValidationErrorsCount() == 0) {
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
            reply.setIsSuccess(false).setMessage("Register attempt failed: Validation failed");
        }
        return reply.build();
    }

}
