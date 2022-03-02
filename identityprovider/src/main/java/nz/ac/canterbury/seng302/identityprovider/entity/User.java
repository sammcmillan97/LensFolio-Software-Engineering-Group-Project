package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;



@Entity
@Table(name="USERS")
public class User {


    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;

    @NotBlank(message="Username is required")
    @Size(max=50, message="Username must be less than 50 characters")
    private String username;

    @NotBlank(message="First name cannot be empty")
    @Size(max=255, message="First name must be less than 255 characters")
    private String firstName;

    @NotBlank(message="Last name cannot be empty")
    @Size(max=255, message="Last name must be less than 255 characters")
    private String lastName;

    @Size(max=255, message="Nickname must be less than 255 characters")
    private String nickname;

    @Size(max=255, message="Bio must be less than 255 characters")
    private String bio;

    //could,should probably use enum
    private String preferredPronouns;

    //probably needs more validation
    @Size(max=255, message="Email can be at most 255 characters")
    @NotBlank(message="Email cannot be empty")
    private String email;

    @NotNull(message="Password must be at least 8 characters")
    @Size(min=8, message="Password must be at least 8 characters")
    private String password;

    protected User() {}

    //with userId as well
    public User(Long userId, String username, String firstName, String lastName, String nickname, String bio, String preferredPronouns, String email, String password){
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.preferredPronouns = preferredPronouns;
        this.email = email;
        this.password = password;
    }

    //without userId
    public User(String username, String firstName, String lastName, String nickname, String bio, String preferredPronouns, String email, String password){
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.preferredPronouns = preferredPronouns;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format (
                "User[userId=%d, username=%s, firstName=%s, lastName=%s, nickname=%s, bio=%s, preferredPronouns=%s, email=%s, password=%s]",
                userId, username, firstName, lastName, nickname, bio, preferredPronouns, email, password);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }
}
