package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;


@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String LONG_NAME_FIELD = "longName";
    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;
//    private static final int TEACHER_GROUP_ID = 420;
//    private static final int MEMBERS_WITHOUT_GROUP_ID = 9999;


    @Autowired
    private GroupRepository groupRepository;

    //Todo Potentially refactor implementation so user repository isn't needed inside group service to reduce coupling.
    @Autowired
    private UserRepository userRepository;

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
        } else if (group.getGroupId() == 9999){
            //TODO The “Members without a group” cant remove members directly from this group to be implemented when this group is added

        } else {
            for(Integer userId: usersIdsToBeRemoved) {
                User user = userRepository.findByUserId(userId);
                group.removeMember(user);
                if(user.getGroups().size() == 0) {
                    addToWithoutAGroup(user);
                }
            }
            reply.setMessage("All members removed");
            reply.setIsSuccess(true);
        }
        return reply.build();
    }

    @VisibleForTesting
    void addToWithoutAGroup(User user) {
        //TODO add to "Members without a group"
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
