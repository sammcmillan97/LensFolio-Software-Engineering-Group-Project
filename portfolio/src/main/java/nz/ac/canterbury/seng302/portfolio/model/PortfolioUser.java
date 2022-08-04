package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user of the portfolio.
 * Only contains information specific to the portfolio, for general user information see the User class.
 */
@Entity
@Table(name="PORTFOLIO_USER")
public class PortfolioUser {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private int userId;
    private String userListSortType;
    private boolean isUserListSortAscending;
    private int currentProject;
    @ElementCollection
    private List<String> skills; //complete list of all skills a user has used

    /**
     * Create a portfolio user.
     * @param userId This should be the same as the user's id from the identity provider.
     * @param userListSortType The sorting type used in the user list page. Should be set to a default.
     */
    public PortfolioUser(int userId, String userListSortType, boolean isUserListSortAscending) {
        this.userId = userId;
        this.userListSortType = userListSortType;
        this.isUserListSortAscending = isUserListSortAscending;
        this.currentProject = 1;
        skills = new ArrayList<>();
    }

    // Empty constructor is needed for JPA
    public PortfolioUser() {
        skills = new ArrayList<>();
    }

    public PortfolioUser(int id){
        this.userId = id;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserListSortType() {
        return userListSortType;
    }

    public void setUserListSortType(String userListSortType) {
        this.userListSortType = userListSortType;
    }

    public boolean isUserListSortAscending() {
        return isUserListSortAscending;
    }

    public void setUserListSortAscending(boolean userListSortAscending) {
        this.isUserListSortAscending = userListSortAscending;
    }

    public void setCurrentProject(int projectId) { this.currentProject = projectId; }

    public int getCurrentProject() { return this.currentProject; }

    public List<String> getSkills() { return skills; }

    public void addSkill (String skill) { this.skills.add(skill); }

    public void addSkills(List<String> skills){
        this.skills.addAll(skills);
    }
}
