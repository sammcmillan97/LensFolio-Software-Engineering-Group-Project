package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class UserAccountsServerService extends UserAccountServiceImplBase {

    private static final String USER_ID_FIELD = "userId";
    private static final String USERNAME_FIELD = "username";
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String MIDDLE_NAME_FIELD = "middleName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String NICKNAME_FIELD = "nickname";
    private static final String BIO_FIELD = "bio";
    private static final String EMAIL_FIELD = "email";
    private static final String PRONOUNS_FIELD = "personalPronouns";
    private static final String PASSWORD_FIELD = "password";
    private static final String CURRENT_PASSWORD_FIELD = "currentPassword";

    @Autowired
    private UserRepository repository;

    /**
     * Checks if the requesting user is authenticated.
     * @return True if the requesting user is authenticated
     */
    private boolean isAuthenticated() {
        AuthState authState = AuthenticationServerInterceptor.AUTH_STATE.get();
        return authState.getIsAuthenticated();
    }

    /**
     * Checks if the requesting user is authenticated as the claimed user.
     * @param claimedId The id of the user that the requesting user claims to be
     * @return True if the requesting user is authenticated as the claimed user
     */
    private boolean isAuthenticatedAsUser(int claimedId) {
        AuthState authState = AuthenticationServerInterceptor.AUTH_STATE.get();
        // The following line needs some explanation.
        // authState.getClaimsList() gets a list of ClaimDTO objects - these are defined in authentication.proto
        // .stream() turns the list into a stream, this is a nice way of processing collections in java
        // .filter() filters the stream to only nameid claims - this is the user's claimed id
        // .findFirst() gets the first nameid claim - there should only be one but we have to consider this possibility
        // .map() takes the ClaimDTO object and converts it to the userId we want
        // .orElse() takes care of the case that no nameid claims were found (like the case when the user is not logged in)
        String authenticatedId = authState.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
        return authState.getIsAuthenticated() && Integer.parseInt(authenticatedId) == claimedId;
    }

    /**
     * If the user is authenticated as the user they want to change the password for, attempt to change their password
     * @param request The request to change the user's password
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        ChangePasswordResponse reply;
        if (isAuthenticatedAsUser(request.getUserId())) {
            reply = changeUserPasswordHandler(request);
        } else {
            reply = ChangePasswordResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Password change failed: Not authenticated")
                    .build();
        }
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

        int userId = request.getUserId();
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        reply.addAllValidationErrors(checkPassword(newPassword));

        List<ValidationError> userValidationErrors = checkUserExists(userId);
        reply.addAllValidationErrors(userValidationErrors);

        if (userValidationErrors.isEmpty()) {
            reply.addAllValidationErrors(checkCurrentPassword(currentPassword, userId));
        }

        if (reply.getValidationErrorsCount() == 0) {
            User user = repository.findByUserId(request.getUserId());
            user.setPassword(newPassword);
            repository.save(user);
            reply.setIsSuccess(true).setMessage("Successfully changed password");

        } else {
            reply.setIsSuccess(false).setMessage("Password change failed: Validation failed");
        }

        return reply.build();
    }

    /**
     * If the user is authenticated as the user they want to edit, attempt to edit the user
     * @param request The request to edit the user
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        EditUserResponse reply;
        if (isAuthenticatedAsUser(request.getUserId())) {
            reply = editUserHandler(request);
        } else {
            reply = EditUserResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Edit user failed: Not authenticated")
                    .build();
        }
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

        int userId = request.getUserId();
        String firstName = request.getFirstName();
        String middleName = request.getMiddleName();
        String lastName = request.getLastName();
        String nickname = request.getNickname();
        String bio = request.getBio();
        String personalPronouns = request.getPersonalPronouns();
        String email = request.getEmail();

        reply.addAllValidationErrors(checkFirstName(firstName));
        reply.addAllValidationErrors(checkMiddleName(middleName));
        reply.addAllValidationErrors(checkLastName(lastName));
        reply.addAllValidationErrors(checkNickname(nickname));
        reply.addAllValidationErrors(checkBio(bio));
        reply.addAllValidationErrors(checkPersonalPronouns(personalPronouns));
        reply.addAllValidationErrors(checkEmail(email));
        reply.addAllValidationErrors(checkUserExists(userId));

        if (reply.getValidationErrorsCount() == 0) {

            User user = repository.findByUserId(userId);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setNickname(nickname);
            user.setBio(bio);
            user.setPersonalPronouns(personalPronouns);
            user.setEmail(email);
            repository.save(user);
            reply.setIsSuccess(true).setMessage("Edit user succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Edit user failed: Validation failed");
        }
        return reply.build();
    }

    /**
     * If the user is authenticated as any valid user, attempt to get the information of the requested user
     * @param request The request to get the user's information
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        UserResponse reply;
        if (isAuthenticated()) {
            reply = getUserAccountByIdHandler(request);
        } else {
            reply = UserResponse.newBuilder().build();
        }
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

    /**
     * Attempt to register a user. Does not check authentication.
     * @param request The request to register a user
     * @param responseObserver The observer to send the response over
     */
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username already taken").setFieldName(USERNAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }

        reply.addAllValidationErrors(checkUsername(username));
        reply.addAllValidationErrors(checkFirstName(firstName));
        reply.addAllValidationErrors(checkMiddleName(middleName));
        reply.addAllValidationErrors(checkLastName(lastName));
        reply.addAllValidationErrors(checkNickname(nickname));
        reply.addAllValidationErrors(checkBio(bio));
        reply.addAllValidationErrors(checkPersonalPronouns(personalPronouns));
        reply.addAllValidationErrors(checkEmail(email));
        reply.addAllValidationErrors(checkPassword(password));

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

    /**
     *  Checks that the username is within the length requirements
     * @param username the username to check
     * @return A list of validation errors found when checking the username
     */
    private List<ValidationError> checkUsername(String username) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (username.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username is required").setFieldName(USERNAME_FIELD).build();
            validationErrors.add(validationError);
        }

        if (username.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username must be less than 65 characters").setFieldName(USERNAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the first name is within the length requirements
     * @param firstName the first name to check
     * @return A list of validation errors found when checking the first name
     */
    private List<ValidationError> checkFirstName(String firstName) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (firstName.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name is required").setFieldName(FIRST_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        if (firstName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name must be less than 65 characters").setFieldName(FIRST_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the middle name is within the length requirements
     * @param middleName the middle name to check
     * @return A list of validation errors found when checking the middle name
     */
    private List<ValidationError> checkMiddleName(String middleName) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (middleName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Middle name must be less than 65 characters").setFieldName(MIDDLE_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the last name is within the length requirements
     * @param lastName the last name to check
     * @return A list of validation errors found when checking the last name
     */
    private List<ValidationError> checkLastName(String lastName) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (lastName.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name is required").setFieldName(LAST_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        if (lastName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name must be less than 65 characters").setFieldName(LAST_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     *  Checks that the nickname is within the length requirements
     * @param nickname the nickname to check
     * @return A list of validation errors found when checking the nickname
     */
    private List<ValidationError> checkNickname(String nickname) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (nickname.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Nickname must be less than 65 characters").setFieldName(NICKNAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the bio is within the length requirement
     * @param bio the bio to check
     * @return A list of validation errors found when checking the bio
     */
    private List<ValidationError> checkBio(String bio) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (bio.length() > 1024) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Bio must be less than 1025 characters").setFieldName(BIO_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the personal pronouns are within the length requirements
     * @param personalPronouns the personal pronouns to check
     * @return A list of validation errors found when checking the personal pronouns
     */
    private List<ValidationError> checkPersonalPronouns(String personalPronouns) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (personalPronouns.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Personal pronouns must be less than 65 characters").setFieldName(PRONOUNS_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the email is within the length requirements and contains an '@' symbol
     * @param email the email to check
     * @return A list of validation errors found when checking the email
     */
    private List<ValidationError> checkEmail(String email) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (email.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email is required").setFieldName(EMAIL_FIELD).build();
            validationErrors.add(validationError);
        } else if (!email.contains("@")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be valid").setFieldName(EMAIL_FIELD).build();
            validationErrors.add(validationError);
        }

        if (email.length() > 255) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be less than 256 characters").setFieldName(EMAIL_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the given password meets the length requirements
     * @param password the password to check
     * @return A list of validation errors found when checking the password
     */
    private List<ValidationError> checkPassword(String password) {
        List<ValidationError> validationErrors = new ArrayList<>();

        if (password.length() < 8) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be at least 8 characters").setFieldName(PASSWORD_FIELD).build();
            validationErrors.add(validationError);
        }

        if (password.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be less than 65 characters").setFieldName(PASSWORD_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the given user id exists
     * @param userId the user id to check
     * @return A list of validation errors found when checking the user id
     */
    private List<ValidationError> checkUserExists(int userId) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (!repository.existsById(userId)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("User does not exist").setFieldName(USER_ID_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks that the given password is the users current password and returns a list of any validation errors found
     * when checking
     *
     * @param currentPassword The current password to be checked
     * @param userId The user id to check the password against (assumed to be valid)
     * @return A list of validation errors found when checking the password
     */
    private List<ValidationError> checkCurrentPassword(String currentPassword, int userId) {
        List<ValidationError> validationErrors = new ArrayList<>();
        User tempUser = repository.findByUserId(userId);
        if (Boolean.FALSE.equals(tempUser.checkPassword(currentPassword))) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Current password is incorrect").setFieldName(CURRENT_PASSWORD_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

}
