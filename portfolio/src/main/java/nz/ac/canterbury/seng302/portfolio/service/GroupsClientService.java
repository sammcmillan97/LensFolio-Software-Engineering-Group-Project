package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

@Service
public class GroupsClientService {

    @GrpcClient("identity-provider-grpc-server")
    private GroupsServiceGrpc.GroupsServiceBlockingStub groupsStub;

    @GrpcClient("identity-provider-grpc-server")
    private GroupsServiceGrpc.GroupsServiceStub groupsNonBlockingStub;

    public CreateGroupResponse createGroup(final String shortName, final String longName) {
        CreateGroupRequest createGroupRequest = CreateGroupRequest.newBuilder()
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsStub.createGroup(createGroupRequest);
    }

    /**
     * Service for getting the group details
     * @param groupId the id of the group to get the details for
     * @return the response from the server
     */
    public GetGroupDetailsResponse getGroupDetails(final int groupId) {
        GetGroupDetailsRequest getGroupDetailsRequest = GetGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsStub.getGroupDetails(getGroupDetailsRequest);
    }

    /**
     * Service for deleting a group
     * @param groupId the id of the group to be deleted
     * @return the response from the server
     */
    public DeleteGroupResponse deleteGroup(final int groupId) {
        DeleteGroupRequest deleteGroupRequest = DeleteGroupRequest.newBuilder()
                .setGroupId(groupId)
                .build();
        return groupsStub.deleteGroup(deleteGroupRequest);
    }
}
