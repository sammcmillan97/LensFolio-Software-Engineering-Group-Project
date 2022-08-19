package nz.ac.canterbury.seng302.portfolio.model.group;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedGroupsResponse;

import java.util.ArrayList;
import java.util.List;

public class GroupListResponse {

    private List<Group> groups;
    private int resultSetSize;

    /**
     * Create a list of groups based on a PaginatedGroupsResponse from the identity provider.
     * They contain the same data, but the GroupListResponse has a list of Groups, not a list of GroupDetailsResponses
     * @param source The PaginatedGroupsResponmse to create a list of users from.
     */
    public GroupListResponse(PaginatedGroupsResponse source) {
        groups = new ArrayList<>();
        for (GroupDetailsResponse groupDetailsResponse : source.getGroupsList()) {
            Group group = new Group(groupDetailsResponse);
            groups.add(group);
        }
        resultSetSize = source.getResultSetSize();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public int getResultSetSize() {
        return resultSetSize;
    }
}
