package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

/**
 * Represents a user of the portfolio.
 * Only contains information specific to the portfolio, for general user information see the User class.
 */
@Entity
@Table(name="PORTFOLIO_USER")
public class PortfolioUser {

    @Id
    private final int id;
    private String userListSortType;

    /**
     * Create a portfolio user.
     * @param id This should be the same as the user's id from the identity provider.
     * @param userListSortType The sorting type used in the user list page. Should be set to a default.
     */
    public PortfolioUser(int id, String userListSortType) {
        this.id = id;
        this.userListSortType = userListSortType;
    }

    public int getId() {
        return id;
    }

    public String getUserListSortType() {
        return userListSortType;
    }

    public void setUserListSortType(String userListSortType) {
        this.userListSortType = userListSortType;
    }

}
