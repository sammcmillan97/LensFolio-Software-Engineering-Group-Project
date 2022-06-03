package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String LONG_NAME_FIELD = "longName";
    private static final String GROUP_ID_FIELD = "groupId";
    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;

    private UserAccountsServerService userAccountsServerService;
//  private static final int TEACHER_GROUP_ID = 420;
    private static final int MEMBERS_WITHOUT_GROUP_ID = 9999;


    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

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

    /**
     * GRPC Method to add a collection of users to a group
     * @param request The client built request containing the group and users
     * @param responseObserver The observer to send the response
     */

    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        AddGroupMembersResponse reply;
        if (isAuthenticated() && isTeacher()) {
            reply = addGroupMembersHandler(request);
        } else {
            reply = AddGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Add group members failed: Not Authorised")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Method for handling adding group members
     * Checks if group exists if so returns
     * checks if group members belongs to the members without a group if so removes them
     * Checks if User exits if so returns
     * @param request The request containing the group and users to be added
     * @return The reply containing message and if it succeeded
     */
    @VisibleForTesting
    AddGroupMembersResponse addGroupMembersHandler(AddGroupMembersRequest request) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();
        int groupId = request.getGroupId();
        Group group = groupRepository.findByGroupId(groupId);
        List<Integer> usersIdsToBeAdded = request.getUserIdsList();
        System.out.println("Reached");
        if(group == null) {
            System.out.println("Group NULL");
            reply.setMessage("Group does not exist");
            reply.setIsSuccess(false);
        } else {
            for (Integer userId : usersIdsToBeAdded) {
                User user = userAccountsServerService.getUserById(userId);
                System.out.println(user.toString());
                group.addMember(user);
                if (user.getGroups().contains(groupRepository.findByGroupId(MEMBERS_WITHOUT_GROUP_ID))) {
                    removeFromWithoutAGroup(user);
                }
            }
            groupRepository.save(group);
            reply.setMessage("All members removed successfully");
            reply.setIsSuccess(true);
            System.out.println("Success");
        }
        return reply.build();
    }


    /**
     * GRPC Method to remove a collection of users to a group
     * @param request The client built request containing the group and users
     * @param responseObserver The observer to send the response
     */
    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        RemoveGroupMembersResponse reply;
        if (isAuthenticated() && isTeacher()) {
            reply = removeGroupMembersHandler(request);
        } else {
            reply = RemoveGroupMembersResponse.newBuilder()
                    .setIsSuccess(false)
                    .setMessage("Remove group members failed: Not Authorised")
                    .build();
        }
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * Method for handling removing group members
     * Checks if a group doesn't exist it will return
     * checks if a user now belongs to zero groups will add to the members without a group
     * Checks if a User doesn't exist it will return
     * @param request The request containing the group and users to be added
     * @return The reply containing message and if it succeeded
     */
    @VisibleForTesting
    RemoveGroupMembersResponse removeGroupMembersHandler(RemoveGroupMembersRequest request) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        int groupId = request.getGroupId();
        Group group = groupRepository.findByGroupId(groupId);
        List<Integer> usersIdsToBeRemoved = request.getUserIdsList();

        if(group == null) {
            reply.setMessage("Group does not exist");
            reply.setIsSuccess(false);
            return reply.build();
        } else if (group.getGroupId() == MEMBERS_WITHOUT_GROUP_ID){
            reply.setMessage("Can't remove members from Members without a group group");
            reply.setIsSuccess(false);
            return reply.build();
        } else {
            for (Integer userId : usersIdsToBeRemoved) {
                User user = userAccountsServerService.getUserById(userId);
                group.removeMember(user);
                if (user.getGroups().size() == 0) {
                    addToWithoutAGroup(user);
                }
            }
        }
        groupRepository.save(group);
        reply.setMessage("All members removed successfully");
        reply.setIsSuccess(true);
        return reply.build();
    }

    @VisibleForTesting
    void addToWithoutAGroup(User user) {
        Group withoutAGroupGroup = groupRepository.findByGroupId(MEMBERS_WITHOUT_GROUP_ID);
        withoutAGroupGroup.addMember(user);
        groupRepository.save(withoutAGroupGroup);
    }

    @VisibleForTesting
    void removeFromWithoutAGroup(User user) {
        Group withoutAGroupGroup = groupRepository.findByGroupId(MEMBERS_WITHOUT_GROUP_ID);
        withoutAGroupGroup.removeMember(user);
        groupRepository.save(withoutAGroupGroup);
    }


    @Override
    public void createGroup (CreateGroupRequest request, StreamObserver<CreateGroupResponse> responseObserver) {
        CreateGroupResponse reply = createGroupHandler(request);
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
                    .setMessage("Group Created");
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
        if (isAuthenticated() && isTeacher()) {
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
}
