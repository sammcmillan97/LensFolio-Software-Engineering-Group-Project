package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nz.ac.canterbury.seng302.shared.identityprovider.UserRole.*;

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
    private static final String ALIAS_SORT = "alias";
    private static final String ROLES_SORT = "roles";
    private static final String USERNAME_SORT = "username";
    private static final String NAME_SORT = "name";

    @Autowired
    private UserRepository repository;

    /**
     * Checks if the requesting user is authenticated.
     * @return True if the requesting user is authenticated
     */
    protected boolean isAuthenticated() {
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
     * Service for getting a paginated list of userResponses for use in the portfolio module.
     * Checks if the current user is authenticated and can make the request then call the handler.
     * @param request The request from the user sent from the UserAccountClientService to request a paginated list of userResponses
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void getPaginatedUsers(GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        PaginatedUsersResponse reply;
        if (isAuthenticated()) {
            reply = getPaginatedUsersHandler(request);
        } else {
            reply = PaginatedUsersResponse.newBuilder().build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * The handler for handling get paginated users request. Will take all User data from the DB as a list of users convert
     * to user responses, sort, paginate and order as requested. Then return the list of user responses
     * @param request The request from the user sent from the UserAccountClientService to request a paginated list of userResponses
     * @return paginatedUserResponseList a list of user responses and the size of the original list of users before pagination
     */
    PaginatedUsersResponse getPaginatedUsersHandler(GetPaginatedUsersRequest request) {
        PaginatedUsersResponse.Builder reply = PaginatedUsersResponse.newBuilder();
        //Get all users from the DB
        Iterable<User> users = repository.findAll();
        ArrayList<UserResponse> userResponseList = new ArrayList<>();
        int count = 0;
        //Create user response list
        for(User user: users) {
            userResponseList.add(getUserAccountByIdHandler(GetUserByIdRequest.newBuilder().setId(user.getUserId()).build()));
        }
        //Sorting the list based on the requested order string
        Comparator<UserResponse> comparator = switch (request.getOrderBy()) {
            case (NAME_SORT) -> //Compare method for ordering by name
                    this::paginatedUsersNameSort;
            case (USERNAME_SORT) -> // Compare method for ordering by username
                    Comparator.comparing(UserResponse::getUsername);
            case (ALIAS_SORT) -> //compare method for ordering by alias
                    Comparator.comparing(UserResponse::getNickname);
            case (ROLES_SORT) -> //Compare method for ordering by roles
                    this::paginatedUsersRolesSort;
            default -> //Default compare method uses sort by name
                    this::paginatedUsersNameSort;
        };
        //Calls the sort method
        userResponseList.sort(comparator);
        //If request is descending need to reverse
        if(!request.getIsAscendingOrder()) {
            Collections.reverse(userResponseList);
        }
        //Paginates the data
        ArrayList<UserResponse> paginatedUserResponseList = new ArrayList<>();
        for(UserResponse user: userResponseList) {
            if (count >= request.getOffset() && count < request.getLimit() + request.getOffset()) {
                paginatedUserResponseList.add(user);
            }
            count += 1;
        }
        //Add final sorted, paginated and ordered list
        reply.addAllUsers(paginatedUserResponseList);
        //Add size of original list for pagination purposes
        reply.setResultSetSize(userResponseList.size());
        return reply.build();
    }

    /**
     * Sorts two users by their full name.
     * @param user1 A UserResponse object representing a user
     * @param user2 A UserResponse object representing another user
     * @return Which full name is greater, or 0 if they are the same.
     */
    int paginatedUsersNameSort(UserResponse user1, UserResponse user2) {
        String user1FullName;
        if (!Objects.equals(user1.getMiddleName(), "")) {
            user1FullName = user1.getFirstName() + " " + user1.getMiddleName() + " " + user1.getLastName();
        } else {
            user1FullName = user1.getFirstName() + " " + user1.getLastName();
        }
        String user2FullName;
        if (!Objects.equals(user2.getMiddleName(), "")) {
            user2FullName = user2.getFirstName() + " " + user2.getMiddleName() + " " + user2.getLastName();
        } else {
            user2FullName = user2.getFirstName() + " " + user2.getLastName();
        }
        return user1FullName.compareTo(user2FullName);
    }

    /**
     * Sorts two users by their roles. Roles are sorted by a points system, for example:
     * Course admin > teacher + student > teacher > student
     * @param user1 A UserResponse object representing a user
     * @param user2 A UserResponse object representing another user
     * @return Which roles are greater, or 0 if they are the same.
     */
    int paginatedUsersRolesSort(UserResponse user1, UserResponse user2) {
        int user1RolePoints = 0;
        Integer user2RolePoints = 0;
        if (user1.getRolesList().contains(UserRole.COURSE_ADMINISTRATOR)) {
            user1RolePoints += 4;
        }
        if (user1.getRolesList().contains(UserRole.TEACHER)) {
            user1RolePoints += 2;
        }
        if (user1.getRolesList().contains(UserRole.STUDENT)) {
            user1RolePoints += 1;
        }
        if (user2.getRolesList().contains(UserRole.COURSE_ADMINISTRATOR)) {
            user2RolePoints += 4;
        }
        if (user2.getRolesList().contains(UserRole.TEACHER)) {
            user2RolePoints += 2;
        }
        if (user2.getRolesList().contains(UserRole.STUDENT)) {
            user2RolePoints += 1;
        }
        return user2RolePoints.compareTo(user1RolePoints);
    }

    /**
     * Allows user to upload their profile photo through bidirectional streaming.
     * Takes a StreamObserver that the client has created and uses it
     * to build a new StreamObserver the client can use to upload the photo.
     * Photo should be sent over the StreamObserver like so:
     *  - metadata first
     *  - actual data split into chunks of max. 2^16 (65536)
     *  - call onCompleted()
     * @param responseObserver a StreamObserver that the client has created
     * @return a StreamObserver that the server has created for the client to use
     */
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
                    if (isAuthenticatedAsUser(metaData.getUserId())) {
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
                    } else {
                        //not authenticated as user, illegal action
                        responseObserver.onError(new IllegalStateException());
                    }

                }
            }

            @Override
            public void onError(Throwable throwableError) {
                // Client should never throw an error, so server does not need to handle them.
            }
        };
    }

    /**
     * Service for deleting a users profile photo with authentication
     * @param request A DeleteUserProfilePhotoRequest according to user_accounts.proto
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void deleteUserProfilePhoto(DeleteUserProfilePhotoRequest request, StreamObserver<DeleteUserProfilePhotoResponse> responseObserver) {
        DeleteUserProfilePhotoResponse response;

        if (isAuthenticatedAsUser(request.getUserId())) {
            response = deleteUserProfilePhotoHandler(request);
        } else {
            response = DeleteUserProfilePhotoResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Delete profile picture failed: Not authenticated")
                    .build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    /**
     * Handler for deleting user's photo. If the photo exists, try to delete it.
     * @param request A DeleteUserProfilePhotoRequest according to user_accounts.proto
     * @return A DeleteUserProfilePhotoResponse with success true if the photo was deleted, or did not exist in the first place.
     */
    DeleteUserProfilePhotoResponse deleteUserProfilePhotoHandler(DeleteUserProfilePhotoRequest request) {
        DeleteUserProfilePhotoResponse response;
        User user = repository.findByUserId(request.getUserId());
        if (user.getProfileImagePath() != null) {
            File oldPhoto = new File("src/main/resources/" + user.getProfileImagePath());
            if (oldPhoto.delete()) {
                user.setProfileImagePath(null);
                repository.save(user);
                response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(true).build();
            } else {
                response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(false).build();
            }
        } else {
            response = DeleteUserProfilePhotoResponse.newBuilder().setIsSuccess(true).build();
        }

        return response;
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
    public UserResponse getUserAccountByIdHandler(GetUserByIdRequest request) {
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
                    .setId(user.getUserId())
                    .addAllRoles(user.getRoles());
            if (user.getProfileImagePath() != null) {
                reply.setProfileImagePath("resources/" + user.getProfileImagePath());
            } else {
                reply.setProfileImagePath("resources/profile-images/default/default.jpg");
            }
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
        } else if (username.isBlank()) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username must not contain only whitespace").setFieldName(USERNAME_FIELD).build();
            validationErrors.add(validationError);
        }

        if (username.length() > 64) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username must be less than 65 characters").setFieldName(USERNAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Checks if a name is valid. Checks against a list of reasonable characters that could appear in names.
     * @param name The name to check
     * @return True if the name is valid
     */
    private boolean isBadName(String name) {
        Pattern namePattern = Pattern.compile("[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆŠŽ∂ð ,.'\\-]+");
        Matcher nameMatcher = namePattern.matcher(name);
        return !nameMatcher.matches();
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
        } else if (firstName.isBlank()) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name must not contain only whitespace").setFieldName(FIRST_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        else if (isBadName(firstName)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("First name must not contain special characters").setFieldName(FIRST_NAME_FIELD).build();
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

        if (!Objects.equals(middleName, "") && isBadName(middleName)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Middle name must not contain special characters").setFieldName(MIDDLE_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
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
        } else if (lastName.isBlank()) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name must not contain only whitespace").setFieldName(LAST_NAME_FIELD).build();
            validationErrors.add(validationError);
        } else if (isBadName(lastName)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Last name must not contain special characters").setFieldName(LAST_NAME_FIELD).build();
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

        Pattern pronounsPattern = Pattern.compile(".{1,15}/.{1,15}"); // matches any/any
        Matcher pronounsMatcher = pronounsPattern.matcher(personalPronouns);
        boolean validPronouns = pronounsMatcher.find();

        if (!validPronouns && !personalPronouns.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Personal pronouns must be of form {pronoun}/{pronoun}").setFieldName(PRONOUNS_FIELD).build();
            validationErrors.add(validationError);
        }

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

        Pattern emailPattern = Pattern.compile(".{1,50}@.{1,50}\\..{1,50}"); // matches any@any.any
        Matcher emailMatcher = emailPattern.matcher(email);
        boolean validEmail = emailMatcher.find();
        if (email.equals("")) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email is required").setFieldName(EMAIL_FIELD).build();
            validationErrors.add(validationError);
        } else if (!validEmail) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Email must be of form a@b.c").setFieldName(EMAIL_FIELD).build();
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
        } else if (password.isBlank()) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Password must not contain only whitespace").setFieldName(PASSWORD_FIELD).build();
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

    /**
     * Service that allows authenticated users to add additional roles to a user
     * @param request The request to add the role from a user
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void addRoleToUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        UserRoleChangeResponse reply;
        if (isAuthenticated() && isValidatedForRole(getAuthStateUserId(), request.getRole())) {
            reply = addRoleToUserHandler(request);
        } else {
            reply = UserRoleChangeResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Unable to add role: Not authenticated")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Abstracted main functionality of add role to user
     * this allows for testing
     * @param request A add role request according to user_accounts.proto
     * @return A modify role response according to user_accounts.proto
     */
    @VisibleForTesting
    UserRoleChangeResponse addRoleToUserHandler(ModifyRoleOfUserRequest request) {
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();

        int userId = request.getUserId();
        UserRole role = request.getRole();

        User user = repository.findByUserId(userId);
        //check user doesn't already have given role
        if (userHasRole(userId, role)){
            reply.setIsSuccess(false)
                    .setMessage("Unable to add role. User already has given role");
        } else {
            user.addRole(role);
            repository.save(user);
            reply.setIsSuccess(true)
                    .setMessage("Role successfully added");
        }
        return reply.build();
    }

    /**
     * Service that allows authenticated users to remove roles from a user
     * @param request The request to remove the role from a user
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void removeRoleFromUser(ModifyRoleOfUserRequest request, StreamObserver<UserRoleChangeResponse> responseObserver) {
        UserRoleChangeResponse reply;
        if (isAuthenticated() && isValidatedForRole(getAuthStateUserId(), request.getRole())) {
            reply = removeRoleFromUserHandler(request);
        } else {
            reply = UserRoleChangeResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Unable to remove role: Not authenticated")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Abstracted main functionality of removing a role from user
     * this allows for testing
     * @param request A remove role request according to user_accounts.proto
     * @return A modify role response according to user_accounts.proto
     */
    @VisibleForTesting
    UserRoleChangeResponse removeRoleFromUserHandler(ModifyRoleOfUserRequest request) {
        UserRoleChangeResponse.Builder reply = UserRoleChangeResponse.newBuilder();

        int userId = request.getUserId();
        UserRole role = request.getRole();

        User user = repository.findByUserId(userId);
        //check user has role that you are attempting to remove
        //check that this is not the users only role
        if (!userHasRole(userId, role)) {
            reply.setIsSuccess(false)
                    .setMessage("Unable to remove role. User doesn't have given role");
        } else if (userHasOneRole(userId)){
            reply.setIsSuccess(false)
                    .setMessage("Unable to remove role. User only has one role");
        } else if (getAuthStateUserId() == userId && role == COURSE_ADMINISTRATOR) {
            reply.setIsSuccess(false)
                    .setMessage("Unable to remove role. Cannot remove own course administrator role");
        } else {
            user.removeRole(role);
            repository.save(user);
            reply.setIsSuccess(true)
                    .setMessage("Role successfully removed");
        }
        return reply.build();
    }

    /**
     * Given a user ID check if user has the correct role to add or remove another role.
     * Specifically, TEACHERS can only change student roles, and COURSE ADMINISTRATORS can change student or teacher roles.
     * @param userId the ID of the user
     * @param role the role we are checking against
     * @return true if the user has permission, false otherwise
     */
    public boolean isValidatedForRole(int userId, UserRole role) {
        User user = repository.findByUserId(userId);
        Set<UserRole> roles = user.getRoles();
        if (role == STUDENT) {
            return roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR);
        } else if (role == TEACHER || role == COURSE_ADMINISTRATOR) {
            return roles.contains(COURSE_ADMINISTRATOR);
        } else {
            return false;
        }
    }

    /**
     * Get the user id of the user who is currently logged in
     * @return The user id of the user who is currently logged in
     */
    @VisibleForTesting
    protected int getAuthStateUserId() {
        String authenticatedId;
        AuthState authState = AuthenticationServerInterceptor.AUTH_STATE.get();
        authenticatedId = authState.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");
        return Integer.parseInt(authenticatedId);
    }

    /**
     * Check if user already has role to ensure no double ups
     * @param userId the ID of the user
     * @param role The role of the user
     * @return true if already has role, else false
     */
    private boolean userHasRole(int userId, UserRole role) {
        User user = repository.findByUserId(userId);
        Set<UserRole> roles;
        roles = user.getRoles();
        for (UserRole userRole : roles) {
            if (userRole == role) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user only has one role,
     * to ensure that the last role isn't removed
     * @param userId the ID of the user
     * @return true if has only one role, else false
     */
    private boolean userHasOneRole(int userId) {
        boolean hasOneRole = false;
        User user = repository.findByUserId(userId);
        if (user.getRoles().size() == 1){
            hasOneRole = true;
        }
        return hasOneRole;
    }

    /**
     * Checks if the user has the teacher or course administrator role
     * @return true if it meets the required conditions or else false
     */
    protected boolean isTeacher() {
        User user = repository.findByUserId(getAuthStateUserId());
        Set<UserRole> roles = user.getRoles();
        for (UserRole userRole : roles) {
            if (userRole == UserRole.TEACHER || userRole == UserRole.COURSE_ADMINISTRATOR) {
                return true;
            }
        }
        return false;
    }

}
