package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.util.HashSet;
import java.util.Set;

public class Group {

    private int groupId;
    private String shortName;
    private String longName;
    private int parentProject;
    private Set<User> members = new HashSet<>();

    /**
     * Create a group based on a GroupDetailsResponse from the identity provider.
     * They contain the same data, so it is a simple translation.
     * @param source The GroupDetailsResponse to create a group from.
     */
    public Group(GroupDetailsResponse source) {
        groupId = source.getGroupId();
        shortName = source.getShortName();
        longName = source.getLongName();
        for (UserResponse userResponse: source.getMembersList()) {
            members.add(new User(userResponse));
        }
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public int getParentProject() {
        return parentProject;
    }

    public void setParentProject(int parentProject) {
        this.parentProject = parentProject;
    }

    public Set<User> getMembers() {
        return members;
    }
}
