package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;


@Entity
@Table(name="USERS")
public class User {

    static final int STRENGTH = 10;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int userId;

    @NotBlank(message="Username is required")
    @Size(max=64, message="Username must be less than 65 characters")
    private String username;

    @NotBlank(message="First name cannot be empty")
    @Size(max=64, message="First name must be less than 65 characters")
    private String firstName;

    @Size(max=64, message="Middle name must be less than 65 characters")
    private String middleName;

    @NotBlank(message="Last name cannot be empty")
    @Size(max=64, message="Last name must be less than 65 characters")
    private String lastName;

    @Size(max=64, message="Nickname must be less than 65 characters")
    private String nickname;

    @Size(max=1024, message="Bio must be less than 1025 characters")
    private String bio;

    @Size(max=64, message="Personal Pronouns must be less than 65 characters")
    private String personalPronouns;

    //probably needs more validation
    @Size(max=255, message="Email must be less than 256 characters")
    @NotBlank(message="Email cannot be empty")
    private String email;

    @NotNull(message="Password cannot be empty")
    @Size(min=8, message="Password must be at least 8 characters")
    @Size(max=64, message="Password must be less than 65 characters")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UserRole> roles = new HashSet<>();

    @Column(length = 1024)
    private Timestamp timeCreated;

    /**
     * Create a user for use in backend database.
     * @param username Username of user
     * @param firstName First name of user
     * @param middleName Middle name of user
     * @param lastName Last name of user
     * @param nickname Nickname of user
     * @param bio Bio of user
     * @param personalPronouns Personal pronouns of user
     * @param email Email of user
     * @param password Password of user
     */
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
        this.timeCreated = this.getCurrentTime();
        this.addRole(UserRole.STUDENT);
    }

    /**
     * User can be created with set userId, and date created for testing purposes.
     * @param userId ID of user
     * @param username Username of user
     * @param firstName First name of user
     * @param middleName Middle name of user
     * @param lastName Last name of user
     * @param nickname Nickname of user
     * @param bio Bio of user
     * @param personalPronouns Personal pronouns of user
     * @param email Email of user
     * @param password Password of user
     * @param timestamp Date user was created
     */
    public User(int userId, String username, String firstName, String middleName, String lastName, String nickname, String bio, String personalPronouns, String email, String password, Timestamp timestamp){
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
        this.timeCreated = timestamp;
        this.addRole(UserRole.STUDENT);
    }

    // Empty constructor is needed for JPA
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

    public void setTimeCreated(Timestamp timeCreated) {this.timeCreated = timeCreated;}

    public Timestamp getTimeCreated(){ return this.timeCreated; }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }



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

    /**
     * Returns the current time
     * @return A google.protobuf.Timestamp object representing the current time
     */
    private Timestamp getCurrentTime(){
        Instant time = Instant.now();
        return Timestamp.newBuilder().setSeconds(time.getEpochSecond())
                .setNanos(time.getNano()).build();
    }



}
