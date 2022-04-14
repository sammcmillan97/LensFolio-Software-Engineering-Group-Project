package nz.ac.canterbury.seng302.portfolio.model;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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

    /**
     * Calculates the full name of a user.
     * This is their first, middle then last name.
     * If they have no middle name simply give their first then last name.
     * @return The full name of a user.
     */
    public String getFullName() {
        if (!Objects.equals(middleName, "")) {
            return firstName + " " + middleName + " " + lastName;
        } else {
            return firstName + " " + lastName;
        }
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

    /**
     * Gets roles in string form. Useful for display on the website.
     * Each role is split into capitalised words.
     * For example, COURSE_ADMINISTRATOR becomes Course Administrator
     * @return The roles in string form
     */
    public Collection<String> getRoleStrings() {
        ArrayList<String> roleStrings = new ArrayList<>();
        for (UserRole role : roles) {
            switch(role) {
                case STUDENT -> roleStrings.add("Student");
                case TEACHER -> roleStrings.add("Teacher");
                case COURSE_ADMINISTRATOR -> roleStrings.add("Course Administrator");
                case UNRECOGNIZED -> roleStrings.add("Unrecognised Role"); // This case should never occur
            }
        }
        roleStrings.sort(Comparator.naturalOrder()); // Ensure the roles are always returned in the same order
        return roleStrings;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    /**
     * Gets the date of user creation, with the months/years since creation added to the end.
     * For use when displaying the date on the website.
     * Example:
     * Member Since: 14 March 2021 (1 year 1 month)
     * @return A string representation of the date since creation
     */
    public String getMemberSince() {
        Instant timeCreated = Instant.ofEpochSecond( created.getSeconds() , created.getNanos() );
        LocalDate localDateCreated = timeCreated.atZone( ZoneId.systemDefault() ).toLocalDate();
        Date dateCreated = java.util.Date.from(localDateCreated.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMMM yyyy");
        String formattedDate = "Member Since: " + dateFormat.format(dateCreated) + " ";
        long totalMonths = ChronoUnit.MONTHS.between(localDateCreated, LocalDate.now());

        long months = totalMonths % 12;
        long years = Math.floorDiv(totalMonths, 12);

        formattedDate += "(";
        if (years > 0) {
            String yearPlural = " years";
            if (years == 1) {
                yearPlural = " year";
            }
            formattedDate += years + yearPlural + " ";
        }

        String monthPlural = " months";
        if(months == 1) {
            monthPlural = " month";
        }
        formattedDate += months + monthPlural + ")";

        return formattedDate;
    }
}
