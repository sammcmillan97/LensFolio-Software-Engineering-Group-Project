package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@GrpcService
public class UserAccountsServerService extends UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Override
    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {
        return new StreamObserver<>() {
            ProfilePhotoUploadMetadata metaData;
            byte[] fileContent = new byte[0];

            @Override
            public void onNext(UploadUserProfilePhotoRequest request) { //This is where we put the server's implementation after receiving each message
                if (request.hasMetaData()) {
                    if (metaData == null) {
                        metaData = request.getMetaData();
                        FileUploadStatusResponse response;
                        if (fileContent.length == 0) {
                            response = FileUploadStatusResponse.newBuilder()
                                    .setStatus(FileUploadStatus.PENDING).setMessage("Pending").build();
                        } else {
                            response = FileUploadStatusResponse.newBuilder()
                                    .setStatus(FileUploadStatus.SUCCESS).setMessage("Success").build();
                        }
                        responseObserver.onNext(response);
                    } else {
                        responseObserver.onError(new IllegalArgumentException());
                    }
                } else {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    try {
                        output.write(fileContent);
                        output.write(request.getFileContent().toByteArray());
                    } catch (IOException e) {
                        responseObserver.onError(e);
                    }

                    fileContent = output.toByteArray();
                }
                FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                        .setStatus(FileUploadStatus.IN_PROGRESS).setMessage("In progress").build();
                responseObserver.onNext(response);

            }

            @Override
            public void onCompleted() { //This is where to put the server's implementation after all messages are sent
                if (metaData == null) {
                     responseObserver.onError(new IllegalStateException());
                } else {
                    // TODO authenticate user
                    User user = repository.findByUserId(metaData.getUserId());
                    if (user.getProfileImagePath() != null) {
                        File oldPhoto = new File("src/main/resources/" + user.getProfileImagePath());
                        if (!oldPhoto.delete()) {
                            responseObserver.onError(new FileNotFoundException());
                        }
                    }
                    user.setProfileImagePath("profile-images/" + user.getUsername() + "." + metaData.getFileType());
                    String filepath = "src/main/resources/" + user.getProfileImagePath();
                    File file = new File(filepath);
                    try (OutputStream os = new FileOutputStream(file)) {
                        os.write(fileContent);
                        repository.save(user);
                        FileUploadStatusResponse response = FileUploadStatusResponse.newBuilder()
                                .setStatus(FileUploadStatus.SUCCESS).setMessage("Success").build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    } catch (Exception e) {
                        responseObserver.onError(e);
                    }
                }
            }

            @Override
            public void onError(Throwable throwableError) {
                // Client should never throw an error, so server does not need to handle them.
            }
        };
    }

    @Override
    public void deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request, StreamObserver<DeleteUserProfilePhotoResponse> responseObserver) {
        // TODO authenticate user
        User user = repository.findByUserId(request.getUserId());
        if (user.getProfileImagePath() != null) {
            File oldPhoto = new File("src/main/resources/" + user.getProfileImagePath());
            if (oldPhoto.delete()) {
                DeleteUserProfilePhotoResponse response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(true).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            } else {
                DeleteUserProfilePhotoResponse response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(false).build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
            }
        }
    }

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

        int userId = request.getUserId();
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();

        reply.addAllValidationErrors(checkPassword(newPassword));

        List<ValidationError> userValidationErrors = checkUserExists(userId);
        reply.addAllValidationErrors(userValidationErrors);

        if (userValidationErrors.size() == 0) {
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
                    .setCreated(user.getTimeCreated())
                    .setProfileImagePath("http://localhost:8080/resources/" + user.getProfileImagePath())
                    .addAllRoles(user.getRoles());
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username is required").setFieldName("username").build();
            validationErrors.add(validationError);
        }

        if (username.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username must be less than 65 characters").setFieldName("username").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name is required").setFieldName("firstName").build();
            validationErrors.add(validationError);
        }
        if (firstName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name must be less than 65 characters").setFieldName("firstName").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Middle name must be less than 65 characters").setFieldName("middleName").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name is required").setFieldName("lastName").build();
            validationErrors.add(validationError);
        }
        if (lastName.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name must be less than 65 characters").setFieldName("lastName").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Nickname must be less than 65 characters").setFieldName("nickname").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Bio must be less than 1025 characters").setFieldName("bio").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Personal pronouns must be less than 65 characters").setFieldName("personalPronouns").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email is required").setFieldName("email").build();
            validationErrors.add(validationError);
        } else if (!email.contains("@")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be valid").setFieldName("email").build();
            validationErrors.add(validationError);
        }

        if (email.length() > 255) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be less than 256 characters").setFieldName("email").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be at least 8 characters").setFieldName("password").build();
            validationErrors.add(validationError);
        }

        if (password.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must be less than 65 characters").setFieldName("password").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("User does not exist").setFieldName("userId").build();
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Current password is incorrect").setFieldName("currentPassword").build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }









}
