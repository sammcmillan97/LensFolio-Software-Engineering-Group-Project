package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.Collection;

/**
 * Representation of a user for use in portfolio.
 * Only contains methods for what the portfolio currently uses.
 * These should be added to if more functionality is needed.
 */
public class User {

    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String bio;
    private String personalPronouns;
    private String email;
    private Collection<UserRole> roles;
    private Timestamp created;
    private String profileImagePath;

    public User(UserResponse source) {
        username = source.getUsername();
        firstName = source.getFirstName();
        middleName = source.getMiddleName();
        lastName = source.getLastName();
        nickname = source.getNickname();
        bio = source.getBio();
        personalPronouns = source.getPersonalPronouns();
        email = source.getEmail();
        roles = source.getRolesList();
        created = source.getCreated();
        profileImagePath = source.getProfileImagePath();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public String getPersonalPronouns() {
        return personalPronouns;
    }

    public String getEmail() {
        return email;
    }

    public Collection<UserRole> getRoles() {
        return roles;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }
}
