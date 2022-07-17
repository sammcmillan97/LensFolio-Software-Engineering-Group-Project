package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;

import java.util.*;

public class Group {

    private int groupId;
    private String shortName;
    private String longName;
    private int parentProject;
    private List<User> members = new ArrayList<>();

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

    public Group() {

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

    public List<User> getMembers() {
        Comparator<User> userComparator = Comparator.comparingInt(User::getId);
        members.sort(userComparator);
        return members;
    }

    public boolean equals(Object groupObject) {
        if (groupObject == null) return false;
        if (groupObject == this) return true;
        if (!(groupObject instanceof Group group)) return false;
        return this.groupId == group.groupId
                && this.parentProject == group.parentProject
                && this.shortName.equals(group.shortName)
                && this.longName.equals(group.longName)
                && this.members.equals(group.members);

    }
}
