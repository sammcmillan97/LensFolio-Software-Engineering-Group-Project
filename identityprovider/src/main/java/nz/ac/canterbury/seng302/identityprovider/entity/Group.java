//package nz.ac.canterbury.seng302.identityprovider.entity;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.util.Collection;
//import java.util.Set;
//
//@Entity
//@Table(name="GROUPS")
//public class Group {
//
//    @Id
//    @GeneratedValue(strategy=GenerationType.AUTO)
//    private int groupId;
//
//    @NotBlank(message="Short name is required")
//    @Size(max=20, message="Short name must be 20 characters or less")
//    private String shortName;
//
//    @NotBlank(message="Long name is required")
//    @Size(max=50, message="Long name must be 50 characters or less")
//    private String longName;
//
//    private int parentProject;
//
//    @ManyToMany()
//    @JoinTable(
//            name="groupMembership",
//            joinColumns = @JoinColumn(name = "groupId"),
//            inverseJoinColumns = @JoinColumn(name = "userId")
//    )
//    private Set<User> memebers;
//
//
//    @ManyToMany(mappedBy = "groups")
//    private Collection<User> Group;
//
//    public Collection<User> getGroup() {
//        return Group;
//    }
//
//    public void setGroup(Collection<User> group) {
//        Group = group;
//    }
//}
