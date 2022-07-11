package nz.ac.canterbury.seng302.portfolio.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.portfolio.model.GroupListResponse;
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

    public ModifyGroupDetailsResponse modifyGroupDetails(final int groupId, final String shortName, final String longName) {
        ModifyGroupDetailsRequest modifyGroupDetailsRequest = ModifyGroupDetailsRequest.newBuilder()
                .setGroupId(groupId)
                .setShortName(shortName)
                .setLongName(longName)
                .build();
        return groupsStub.modifyGroupDetails(modifyGroupDetailsRequest);
    }

    /**
     * Service for getting the group details
     * @param groupId the id of the group to get the details for
     * @return the response from the server
     */
    public GroupDetailsResponse getGroupDetails(final int groupId) {
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

    /**
     * Client service method for adding Users to groups
     * @param groupId The group ID which users will be added to
     * @param userIds The list of user ID which will be added to the group
     * @return The response from the IDP
     */
    public AddGroupMembersResponse addGroupMembers(final int groupId, final List<Integer> userIds) {
        AddGroupMembersRequest addGroupMembersRequest = AddGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsStub.addGroupMembers(addGroupMembersRequest);
    }

    public RemoveGroupMembersResponse removeGroupMembers(final int groupId, final List<Integer> userIds) {
        RemoveGroupMembersRequest removeGroupMembersRequest = RemoveGroupMembersRequest.newBuilder()
                .setGroupId(groupId)
                .addAllUserIds(userIds)
                .build();
        return groupsStub.removeGroupMembers(removeGroupMembersRequest);
    }


    /**
     * Creates a request to be sent to the IDP for requesting a paginated list of group responses
     * @param offset The number of groups to be sliced from the original list of groups from the DB
     * @param limit The max number of groups to be returned
     * @param orderBy How the list of groups will be sorted: "short" = sort by short names, "long" = sort by long name, "members" = sort by number of members
     * @param isAscending Whether the list is ascending or descending
     * @return A list of paginated, sorted and ordered group responses
     */
    public GroupListResponse getPaginatedGroups(int offset, int limit, String orderBy, boolean isAscending) {
        GetPaginatedGroupsRequest getPaginatedGroupsRequest = GetPaginatedGroupsRequest.newBuilder()
                .setOffset(offset)
                .setLimit(limit)
                .setOrderBy(orderBy)
                .setIsAscendingOrder(isAscending)
                .build();
        PaginatedGroupsResponse response = groupsStub.getPaginatedGroups(getPaginatedGroupsRequest);
        return new GroupListResponse(response);
    }

    /**
     * Gets all groups in a list of group responses. Uses the getPaginatedGroups method twice, first to get the number of
     * groups in the database, then to get all groups
     * @return A list of group responses, ordered by short name, in ascending order
     */
    public GroupListResponse getAllGroups() {
        GroupListResponse response = getPaginatedGroups(0, 0, "short", true);
        int numGroupsInDb = response.getResultSetSize();
        return getPaginatedGroups(0, numGroupsInDb, "short", true);

    }
}
