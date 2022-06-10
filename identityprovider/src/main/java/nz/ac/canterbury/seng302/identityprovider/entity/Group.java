package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="GROUPS")
public class Group {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int groupId;

    @NotBlank(message="Short name is required")
    @Size(max=20, message="Short name must be 20 characters or less")
    private String shortName;

    @NotBlank(message="Long name is required")
    @Size(max=50, message="Long name must be 50 characters or less")
    private String longName;

    private int parentProject;

    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> members = new HashSet<>();


    /**
     * Constructor for testing purposes
     * @param shortName Short name e.g. Team400
     * @param longName Long name e.g. BadRequest
     * @param parentProject Parent project id as projects encapsulate groups
     */
    public Group(String shortName, String longName, int parentProject) {
        this.shortName = shortName;
        this.longName = longName;
        this.parentProject = parentProject;
    }

    /**
     * Constructor with ID for testing purposes
     */
    public Group(int groupId, String shortName, String longName, int parentProject) {
        this.groupId = groupId;
        this.shortName = shortName;
        this.longName = longName;
        this.parentProject = parentProject;
    }

    /**
     * Empty constructor for JPA
     */
    protected Group() {

    }

    public Group(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
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

    public void addMember(User user) {
        this.members.add(user);
        user.joinGroup(this);
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.leaveGroup(this);
    }

    public Set<User> getMembers() {
      return this.members;
    }

    @Override
    public String toString() {
        return "Group{" +
                "shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", parentProject=" + parentProject +
                '}';
    }

    /**
     * Removes all users from the group before deleting it
     * Without this method repository.deleteAll() doesn't remove groups with users
     */
    @PreRemove
    private void removeUsersFromGroups() {
        for (User u : members) {
            u.leaveGroup(this);
        }
    }
}
