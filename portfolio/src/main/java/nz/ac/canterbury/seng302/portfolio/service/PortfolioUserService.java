package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioUserService {

    @Autowired
    private PortfolioUserRepository repository;

    /**
     * Gets a user by their id. Creates a default user with that id if none exists.
     * @param id The user's id from the identity provider
     * @return The user's portfolio information
     */
    public PortfolioUser getUserById(int id) {
        PortfolioUser user = repository.findByUserId(id);
        if(user != null) {
            return user;
        } else {
            // Ascending by name (nameA) is the default user list sort type
            PortfolioUser newUser = new PortfolioUser(id, "nameA");
            repository.save(newUser);
            return newUser;
        }
    }

    /**
     * Gets a user's user list sort type. This is for the user list page.
     * Creates a default user with that id if none exists.
     * @param id The user's id from the identity provider
     * @return The user's current user list sort type
     */
    public String getUserListSortType(int id) {
        PortfolioUser user = getUserById(id);
        return user.getUserListSortType();
    }

    /**
     * Sets a user's user list sort type. This is for the user list page.
     * Creates a default user with that id if none exists.
     * @param id The user's id from the identity provider
     * @param userListSortType The sort type to change to
     */
    public void setUserListSortType(int id, String userListSortType) {
        PortfolioUser user = getUserById(id);
        user.setUserListSortType(userListSortType);
        repository.save(user);
    }

}
