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

    public ModifyGroupDetailsResponse modifyGroupDetails(final int groupId, final String shortName, final String longName) {
        ModifyGroupDetailsRequest modifyGroupDetailsRequest = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsStub.modifyGroupDetails(modifyGroupDetailsRequest);
    }
}
