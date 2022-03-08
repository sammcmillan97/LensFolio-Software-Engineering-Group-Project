package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.security.SecureRandom;


@Entity
@Table(name="USERS")
public class User {

    static final int STRENGTH = 10;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int userId;

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

    @Size(max=16, message="Personal Pronouns must be less than 16 characters")
    private String personalPronouns;

    //probably needs more validation
    @Size(max=255, message="Email can be at most 255 characters")
    @NotBlank(message="Email cannot be empty")
    private String email;

    @NotNull(message="Password must be at least 8 characters")
    @Size(min=8, message="Password must be at least 8 characters")
    private String password;

    public User(String username, String firstName, String middleName, String lastName, String nickname, String bio, String personalPronouns, String email, String password){
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
        this.email = email;
        this.password = encryptPassword(password);
    }

    public User(int userId, String username, String firstName, String middleName, String lastName, String nickname, String bio, String personalPronouns, String email, String password){
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
        this.email = email;
        this.password = encryptPassword(password);
    }

    protected User() {
    }

    @Override
    public String toString() {
        return String.format (
                "User[userId=%d, username=%s, firstName=%s, middleName=%s, lastName=%s, nickname=%s, bio=%s, preferredPronouns=%s, email=%s]",
                userId, username, firstName, middleName, lastName, nickname, bio, personalPronouns, email);
    }

    public int getUserId() {
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

    public String getPersonalPronouns(){
        return personalPronouns;
    }

    public void setPersonalPronouns(String personalPronouns) {
        this.personalPronouns = personalPronouns;
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
        this.password = encryptPassword(password);}


    /**
     * https://docs.spring.io/spring-security/site/docs/3.2.3.RELEASE/apidocs/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html
     * @param password raw password that user has selected during registration
     * @return encrypted password to be stored in db
     */
    private String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(STRENGTH, new SecureRandom());
        return passwordEncoder.encode(password);
    }

    /**
     * Check password when user logs in
     * @param password raw password user has entered while trying to login
     * @return true if password is correct, otherwise false
     */
    public Boolean checkPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(STRENGTH);
        return passwordEncoder.matches(password, this.password);
    }

}
