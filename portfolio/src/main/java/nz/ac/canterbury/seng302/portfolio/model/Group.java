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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupId == group.groupId && parentProject == group.parentProject && shortName.equals(group.shortName) && longName.equals(group.longName) && members.equals(group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, shortName, longName, parentProject, members);
    }

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

    public Group(int id, String shortname, String longname, int parentproject, List<User> listOfMembers){
        groupId = id;
        shortName = shortname;
        longName = longname;
        parentProject = parentproject;
        members = listOfMembers;
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

}
