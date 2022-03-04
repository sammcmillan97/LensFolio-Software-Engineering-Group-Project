package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message="Middle name cannot be empty")
    @Size(max=255, message="Middle name must be less than 255 characters")
    private String middleName;

    @NotBlank(message="Last name cannot be empty")
    @Size(max=255, message="Last name must be less than 255 characters")
    private String lastName;

    @Size(max=255, message="Nickname must be less than 255 characters")
    private String nickname;

    @Size(max=255, message="Bio must be less than 255 characters")
    private String bio;

    @Enumerated(EnumType.STRING)
    private Pronouns preferredPronouns;

    //probably needs more validation
    @Size(max=255, message="Email can be at most 255 characters")
    @NotBlank(message="Email cannot be empty")
    private String email;

    @NotNull(message="Password must be at least 8 characters")
    @Size(min=8, message="Password must be at least 8 characters")
    private String password;

    protected User() {}

    //with userId as well
    public User(Long userId, String username, String firstName, String middleName, String lastName, String nickname, String bio, Pronouns preferredPronouns, String email, String password){
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.preferredPronouns = preferredPronouns;
        this.email = email;
        this.password = password;
    }

    //without userId
    public User(String username, String firstName, String middleName, String lastName, String nickname, String bio, Pronouns preferredPronouns, String email, String password){
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
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
                "User[userId=%d, username=%s, firstName=%s, middleName=%s, lastName=%s, nickname=%s, bio=%s, preferredPronouns=%s, email=%s, password=%s]",
                userId, username, firstName, middleName, lastName, nickname, bio, preferredPronouns, email, password);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio(){
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Pronouns getPreferredPronouns(){
        return preferredPronouns;
    }

    public void setPreferredPronouns(Pronouns preferredPronouns) {
        this.preferredPronouns = preferredPronouns;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
