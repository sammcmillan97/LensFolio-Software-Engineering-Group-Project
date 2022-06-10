package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Stream;

@GrpcService
public class GroupsServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String LONG_NAME_FIELD = "longName";
    private static final String GROUP_ID_FIELD = "groupId";
    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;
    private static final String SHORT_NAME_SORT = "short";
    private static final String LONG_NAME_SORT = "long";
    private static final String NUM_MEMBERS_SORT = "members";
    
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAccountsServerService userAccountsServerService;

    /**
     * Checks if the requesting user is authenticated.
     * @return True if the requesting user is authenticated
     */
    private boolean isAuthenticated() {
        AuthState authState = AuthenticationServerInterceptor.AUTH_STATE.get();
        return authState.getIsAuthenticated();
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
     * Checks if the user has the teacher or course administrator role
     * @return true if it meets the required conditions or else false
     */
    public boolean isTeacher() {
        User user = userRepository.findByUserId(getAuthStateUserId());
        Set<UserRole> roles = user.getRoles();
        for (UserRole userRole : roles) {
            if (userRole == UserRole.TEACHER || userRole == UserRole.COURSE_ADMINISTRATOR) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void createGroup (CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse reply;
        if (userAccountsServerService.isAuthenticated() && userAccountsServerService.isTeacher()) {
            reply = createGroupHandler(request);
        } else {
            reply = CreateGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Create group failed: User Not Authenticated")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @VisibleForTesting
    CreateGroupResponse createGroupHandler(CreateGroupRequest request){
        CreateGroupResponse.Builder reply = CreateGroupResponse.newBuilder();
        String shortName = request.getShortName();
        String longName = request.getLongName();

        if (groupRepository.findByShortName(shortName) != null) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Group short name already in use").setFieldName(SHORT_NAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }
        if (groupRepository.findByLongName(longName) != null) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Group long name already in use").setFieldName(LONG_NAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }

        reply.addAllValidationErrors(checkShortName(shortName));
        reply.addAllValidationErrors(checkLongName(longName));

        if (reply.getValidationErrorsCount() == 0) {
            groupRepository.save(new Group(shortName, longName));
            reply
                    .setIsSuccess(true)
                    .setNewGroupId(groupRepository.findByShortName(request.getShortName()).getGroupId())
                    .setMessage("Successfully created group");
        } else {
            reply
                    .setIsSuccess(false)
                    .setMessage("Create group failed: Validation failed");
        }
        return reply.build();
    }

    /**
     * The gRPC method that deletes the group
     * @param request the request to get the id of the group to be deleted
     * @param responseObserver the observer to send the response
     */
    @Override
    public void deleteGroup(DeleteGroupRequest request, StreamObserver<DeleteGroupResponse> responseObserver) {
        DeleteGroupResponse reply;
        if (userAccountsServerService.isAuthenticated() && userAccountsServerService.isTeacher()) {
            reply = deleteGroupHandler(request);
        } else {
            reply = DeleteGroupResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Delete group failed: User Not Authenticated")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * The handler for the method to delete a group.
     * @param request the request to get the id of the group to be deleted
     * @return the response built
     */
    @VisibleForTesting
    DeleteGroupResponse deleteGroupHandler(DeleteGroupRequest request) {
        DeleteGroupResponse.Builder reply = DeleteGroupResponse.newBuilder();
        int groupId = request.getGroupId();

        if (groupRepository.findByGroupId(groupId) != null) {
            groupRepository.deleteById(groupId);
            reply
                    .setIsSuccess(true)
                    .setMessage("Group deleted successfully");
        } else {
            reply
                    .setIsSuccess(false)
                    .setMessage("Deleting group failed: Group does not exist");
        }
        return reply.build();
    }

    @Override
    public void modifyGroupDetails (ModifyGroupDetailsRequest request, StreamObserver<ModifyGroupDetailsResponse> responseObserver) {
        ModifyGroupDetailsResponse reply;
        if (userAccountsServerService.isAuthenticated() && userAccountsServerService.isTeacher()) {
            reply = modifyGroupDetailsHandler(request);
        } else {
            reply = ModifyGroupDetailsResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Modify group failed: User Not Authenticated")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @VisibleForTesting
    ModifyGroupDetailsResponse modifyGroupDetailsHandler (ModifyGroupDetailsRequest request){
        ModifyGroupDetailsResponse.Builder reply = ModifyGroupDetailsResponse.newBuilder();
        int groupId = request.getGroupId();
        String shortName = request.getShortName();
        String longName = request.getLongName();

        if (groupRepository.findByGroupId(groupId) == null) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Group does not exist").setFieldName(GROUP_ID_FIELD).build();
            reply.addValidationErrors(validationError);
        }
        if (groupRepository.findByShortName(shortName) != null && (groupRepository.findByShortName(shortName).getGroupId() != groupId)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Group short name already in use").setFieldName(SHORT_NAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }
        if (groupRepository.findByLongName(longName) != null && (groupRepository.findByLongName(longName).getGroupId() != groupId)) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Group long name already in use").setFieldName(LONG_NAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }

        reply.addAllValidationErrors(checkShortName(shortName));
        reply.addAllValidationErrors(checkLongName(longName));

        if (reply.getValidationErrorsCount() == 0) {
            Group group = groupRepository.findByGroupId(groupId);
            group.setShortName(shortName);
            group.setLongName(longName);
            groupRepository.save(group);
            reply
                    .setIsSuccess(true)
                    .setMessage("Successfully modified group");
        } else {
            reply
                    .setIsSuccess(false)
                    .setMessage("Modify group failed: Validation failed");
        }
        return reply.build();
    }

    /**
     * The gRPC method which gets the group details if user is authenticated
     * @param request the request to get the id of the group using which we get the information
     * @param responseObserver the observer to send the response
     */
    @Override
    public void getGroupDetails(GetGroupDetailsRequest request, StreamObserver<GroupDetailsResponse> responseObserver) {
        GroupDetailsResponse reply;
        if (isAuthenticated()) {
            reply = getGroupDetailsHandler(request);
        } else {
            reply = GroupDetailsResponse.newBuilder().build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * The handler for the method which gets the group details
     * @param request the request which receives the id from the proto file
     * @return the response built
     */
    @VisibleForTesting
    GroupDetailsResponse getGroupDetailsHandler(GetGroupDetailsRequest request) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();
        int groupId = request.getGroupId();

        if (groupRepository.existsById(groupId)) {
            Group group = groupRepository.findByGroupId(groupId);
            Set<User> members = group.getMembers();
            List<UserResponse> userResponses = new ArrayList<>();
            for (User member : members) {
                userResponses.add(member.toUserResponse());
            }
            reply
                    .setShortName(group.getShortName())
                    .setLongName(group.getLongName())
                    .addAllMembers(userResponses);
        }
        return reply.build();
    }


    private List<ValidationError> checkLongName(String longName) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (longName.length()>LONG_NAME_MAX_LENGTH){
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Long name must be less than " + LONG_NAME_MAX_LENGTH + "chars").setFieldName(LONG_NAME_FIELD).build();
            validationErrors.add(validationError);
        } else if (longName.isEmpty()){
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Long name cannot be empty").setFieldName(LONG_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    private List<ValidationError> checkShortName(String shortName) {
        List<ValidationError> validationErrors = new ArrayList<>();
        if (shortName.length() > SHORT_NAME_MAX_LENGTH) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Short name must be less than " + SHORT_NAME_MAX_LENGTH + " chars").setFieldName(SHORT_NAME_FIELD).build();
            validationErrors.add(validationError);
        } else if (shortName.isEmpty()) {
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Short name cannot be empty").setFieldName(SHORT_NAME_FIELD).build();
            validationErrors.add(validationError);
        }
        return validationErrors;
    }

    /**
     * Service for getting a paginated list of groupResponses for use in the portfolio module
     * Checks if the current user is authenticated and can make the request, then calls the handler
     * @param request The request from the user sent from GroupsClientService to request a paginated list of groupResponses
     * @param responseObserver The observer to send the response over
     */
    @Override
    public void getPaginatedGroups(GetPaginatedGroupsRequest request, StreamObserver<PaginatedGroupsResponse> responseObserver) {
        PaginatedGroupsResponse reply;
        if (userAccountsServerService.isAuthenticated()) {
            reply = getPaginatedGroupsHandler(request);
        } else {
            reply = PaginatedGroupsResponse.newBuilder().build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * The handler for handling get paginated groups request. Will take all Group data from the DB as a list of groups,
     * convert to group details responses, sort, paginate and order as requested, then return the list of group details
     * responses
     * @param request The request from the user sent from GroupsClientService to request a paginated list of groupResponses
     * @return paginatedGroupResponse a list of group responses and the size of the original list of groups before pagination
     */
    @VisibleForTesting
    protected PaginatedGroupsResponse getPaginatedGroupsHandler(GetPaginatedGroupsRequest request) {
        PaginatedGroupsResponse.Builder reply = PaginatedGroupsResponse.newBuilder();
        // Get all groups from the database
        Iterable<Group> groups = groupRepository.findAll();
        List<GroupDetailsResponse> groupDetailsResponses = new ArrayList<>();

        for (Group group : groups) {
            groupDetailsResponses.add(getGroupByIdHandler(GetGroupDetailsRequest.newBuilder().setGroupId(group.getGroupId()).build()));
        }

        // Create a comparator based on the requested sort order
        Comparator<GroupDetailsResponse> comparator = switch (request.getOrderBy()) {
            case (SHORT_NAME_SORT) ->
                Comparator.comparing(GroupDetailsResponse::getShortName);
            case (LONG_NAME_SORT) ->
                Comparator.comparing(GroupDetailsResponse::getLongName);
            case (NUM_MEMBERS_SORT) ->
                Comparator.comparing(GroupDetailsResponse::getMembersCount);
            default ->
                Comparator.comparing(GroupDetailsResponse::getShortName);
        };

        // Sort the list by the desired method
        groupDetailsResponses.sort(comparator);

        // Reverse the list if request asked for descending order
        if (!request.getIsAscendingOrder()) {
            Collections.reverse(groupDetailsResponses);
        }

        List<GroupDetailsResponse> paginatedGroupDetailsResponses = new ArrayList<>();

        // Paginate the data
        int count = 0;
        for (GroupDetailsResponse group : groupDetailsResponses) {
            if (count >= request.getOffset() && count < request.getLimit() + request.getOffset()) {
                paginatedGroupDetailsResponses.add(group);
            }
            count += 1;
        }

        // Add sorted, ordered and paginated list of groups
        reply.addAllGroups(paginatedGroupDetailsResponses);

        // Add number of all groups for pagination purposes
        reply.setResultSetSize(groupDetailsResponses.size());

        return reply.build();
    }

    /**
     * Handler for group information retrieval requests
     * If the group id exists, return their information
     * Else, return blank information
     * @param request A get group details by id request according to groups.proto
     * @return A group details response according to groups.proto
     */
    @VisibleForTesting
    GroupDetailsResponse getGroupByIdHandler(GetGroupDetailsRequest request) {
        GroupDetailsResponse.Builder reply = GroupDetailsResponse.newBuilder();

        if (groupRepository.existsById(request.getGroupId())) {
            Group group = groupRepository.findByGroupId(request.getGroupId());

            List<UserResponse> members = new ArrayList<>();
            for (User user : group.getMembers()) {
                members.add(userAccountsServerService.getUserAccountByIdHandler(GetUserByIdRequest.newBuilder().setId(user.getUserId()).build()));
            }
            reply.setGroupId(group.getGroupId())
                    .setShortName(group.getShortName())
                    .setLongName(group.getLongName())
                    .addAllMembers(members);
        }
        return reply.build();
    }

    public List<UserResponse> convertSetOfUsersToUserResponse(Set<User> members) {
        List<UserResponse> userResponses = new ArrayList<>();
        for (User member : members) {
            userResponses.add(member.toUserResponse());
        }
        return userResponses;
    }
}
