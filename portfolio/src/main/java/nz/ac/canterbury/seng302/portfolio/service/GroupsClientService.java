package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public AddGroupMembersResponse removeGroupMembers(final int groupId, final List<Integer> userIds) {
        AddGroupMembersRequest addGroupMembersRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .setUserIds(1, 2)
                .addAllUserIds(userIds)
                .build();
        return groupsStub.addGroupMembers(addGroupMembersRequest);
    }
}
