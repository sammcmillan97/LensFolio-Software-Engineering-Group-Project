package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class PortfolioUserService {

    @Autowired
    private PortfolioUserRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

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
            // Ascending by name is the default user list sort
            PortfolioUser newUser = new PortfolioUser(id, "name", true);
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

    /**
     * Gets whether a user's user list sort is ascending. This is for the user list page.
     * Creates a default user with that id if none exists.
     * @param id The user's id from the identity provider
     * @return Whether a user's user list sort is ascending
     */
    public boolean isUserListSortAscending(int id) {
        PortfolioUser user = getUserById(id);
        return user.isUserListSortAscending();
    }

    /**
     * Sets whether a user's user list sort is ascending. This is for the user list page.
     * Creates a default user with that id if none exists.
     * @param id The user's id from the identity provider
     * @param userListSortIsAscending Whether a user's user list sort should be ascending
     */
    public void setUserListSortAscending(int id, boolean userListSortIsAscending) {
        PortfolioUser user = getUserById(id);
        user.setUserListSortAscending(userListSortIsAscending);
        repository.save(user);
    }

    /**
     * Get authenticated user's current project
     * @param userId of authenticated user
     * @return current selected project else first in project repo
     */
    public Project getCurrentProject(int userId){
        PortfolioUser portfolioUser = repository.findByUserId(userId);
        int id = portfolioUser.getCurrentProject();
        Project project = projectRepository.findById(id);
        if (project==null){
            return projectRepository.findAll().iterator().next();
        } else {
            return project;
        }

    }

    /**
     *
     * @param userId
     * @param projectId
     */
    public void setProject(int userId, int projectId){
        PortfolioUser portfolioUser = repository.findByUserId(userId);
        if (portfolioUser==null) {
            PortfolioUser portfolioUser1 = new PortfolioUser(userId);
            repository.save(portfolioUser1);
        }
        portfolioUser.setCurrentProject(projectId);
        repository.save(portfolioUser);
    }

    /**
     * Get a list of skills from a Portfolio User
     * @param userId unique id of User from idp, and PortfolioUser
     * @return list of their skills
     */
    public List<String> getPortfolioUserSkills(int userId){
        PortfolioUser portfolioUser = getUserById(userId);
        if (portfolioUser==null){
            return null;
        } else {
            return portfolioUser.getSkills();
        }
    }

    /**
     * Iterate through the list of skills, and add to the given user
     * @param userId unique id of User from idp, and PortfolioUser
     * @param skills list of skills being added
     */
    public void addPortfolioUserSkills(int userId, List<String> skills){
        PortfolioUser portfolioUser = getUserById(userId);
        if (portfolioUser==null){
            throw new IllegalArgumentException("User not found");
        } else {
            Iterator<String> skillsIterator = skills.iterator();
            while(skillsIterator.hasNext()){
                portfolioUser.addSkill(skillsIterator.next());
            }
        }
    }

}
