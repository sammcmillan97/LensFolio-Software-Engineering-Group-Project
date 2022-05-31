package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountsServerService;


@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String LONG_NAME_FIELD = "longName";
    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;
    private UserAccountsServerService userAccountsServerService;
    private static final int TEACHER_GROUP_ID = 420;
    private static final int MEMBERS_WITHOUT_GROUP_ID = 9999;


    @Autowired
    private GroupRepository groupRepository;

    public GroupServerService() {
    }


    @Override
    public void addGroupMembers(AddGroupMembersRequest request, StreamObserver<AddGroupMembersResponse> responseObserver) {
        AddGroupMembersResponse reply = addGroupMembersHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @VisibleForTesting
    AddGroupMembersResponse addGroupMembersHandler(AddGroupMembersRequest request) {
        AddGroupMembersResponse.Builder reply = AddGroupMembersResponse.newBuilder();
        int groupId = request.getGroupId();
        Group group = groupRepository.findByGroupId(groupId);
        Iterable<Integer> usersIdsToBeAdded = request.getUserIdsList();

        if(group == null) {
            reply.setMessage("Group does not exist");
            reply.setIsSuccess(false);
        } else {
            for (Integer userId : usersIdsToBeAdded) {
                try {
                    User user = userAccountsServerService.getUserById(userId);
                    group.addMember(user);
                    if (user.getGroups().contains(groupRepository.findByGroupId(MEMBERS_WITHOUT_GROUP_ID))) {
                        removeFromWithoutAGroup(user);
                    }
                } catch (NullPointerException e) {
                    reply.setMessage("User id: " + userId + " does not exist");
                    reply.setIsSuccess(false);
                    return reply.build();
                }
            }
            groupRepository.save(group);
            reply.setMessage("All members removed successfully");
            reply.setIsSuccess(true);
        }
        return reply.build();
    }


    @Override
    public void removeGroupMembers(RemoveGroupMembersRequest request, StreamObserver<RemoveGroupMembersResponse> responseObserver) {
        RemoveGroupMembersResponse reply = removeGroupMembersHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @VisibleForTesting
    RemoveGroupMembersResponse removeGroupMembersHandler(RemoveGroupMembersRequest request) {
        RemoveGroupMembersResponse.Builder reply = RemoveGroupMembersResponse.newBuilder();
        int groupId = request.getGroupId();
        Group group = groupRepository.findByGroupId(groupId);
        Iterable<Integer> usersIdsToBeRemoved = request.getUserIdsList();

        if(group == null) {
            reply.setMessage("Group does not exist");
            reply.setIsSuccess(false);
        } else if (group.getGroupId() == MEMBERS_WITHOUT_GROUP_ID){
            reply.setMessage("Can't remove members from Memebers without a group group");
            reply.setIsSuccess(false);
        }
        else {
                for (Integer userId : usersIdsToBeRemoved) {
                    try {
                        User user = userAccountsServerService.getUserById(userId);
                        group.removeMember(user);
                        if (user.getGroups().size() == 0) {
                            addToWithoutAGroup(user);
                        }
                    } catch (NullPointerException e) {
                        reply.setMessage("User id: " + userId + " does not exist");
                        reply.setIsSuccess(false);
                        return reply.build();
                    }
                }

            groupRepository.save(group);
            reply.setMessage("All members removed successfully");
            reply.setIsSuccess(true);
        }
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
            ValidationError validationError = ValidationError.newBuilder().setErrorText("Username already taken").setFieldName(LONG_NAME_FIELD).build();
            reply.addValidationErrors(validationError);
        }

        reply.addValidationErrors(checkShortName(shortName));
        reply.addValidationErrors(checkLongName(longName));

        if (reply.getValidationErrorsCount() == 0) {
            groupRepository.save(new Group(shortName, longName));
            reply
                    .setIsSuccess(true)
                    .setNewGroupId(groupRepository.findByShortName(request.getShortName()).getGroupId())
                    .setMessage("Group Created");
        } else {
            reply
                    .setIsSuccess(false)
                    .setMessage("Invalid request, unable to create group");
        }
        return reply.build();
    }

    private ValidationError checkLongName(String longName) {
        ValidationError validationError = null;
        if (longName.length()>LONG_NAME_MAX_LENGTH){
            validationError = ValidationError.newBuilder().setErrorText("Long name must be less than " + LONG_NAME_MAX_LENGTH + "chars").setFieldName(LONG_NAME_FIELD).build();
        } else if (longName.isEmpty()){
            validationError = ValidationError.newBuilder().setErrorText("Long name cannot be empty").setFieldName(LONG_NAME_FIELD).build();
        }
        return validationError;
    }

    private ValidationError checkShortName(String shortName) {
        ValidationError validationError = null;
        if (shortName.length() > SHORT_NAME_MAX_LENGTH) {
            validationError = ValidationError.newBuilder().setErrorText("Short name must be less than " + SHORT_NAME_MAX_LENGTH + " chars").setFieldName(SHORT_NAME_FIELD).build();
        } else if (shortName.isEmpty()) {
            validationError = ValidationError.newBuilder().setErrorText("Short name cannot be empty").setFieldName(SHORT_NAME_FIELD).build();
        }
        return validationError;
    }

}
