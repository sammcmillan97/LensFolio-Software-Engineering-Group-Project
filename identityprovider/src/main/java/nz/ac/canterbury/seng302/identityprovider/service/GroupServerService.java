package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.CreateGroupResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@GrpcService
public class GroupServerService extends GroupsServiceGrpc.GroupsServiceImplBase {

    private static final String SHORT_NAME_FIELD = "shortName";
    private static final String LONG_NAME_FIELD = "longName";
    private static final int SHORT_NAME_MAX_LENGTH = 32;
    private static final int LONG_NAME_MAX_LENGTH = 128;


    @Autowired
    private GroupRepository groupRepository;

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
