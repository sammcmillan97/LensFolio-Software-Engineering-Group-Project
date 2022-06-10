package nz.ac.canterbury.seng302.portfolio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a refresh that need to be applied to all users viewing the project.
 */
public class ProjectRefresh {

    private final int projectId;
    private final List<Integer> usersSeenRefresh;
    private final long time;

    /**
     * Create a new project refresh.
     * It will time out after 5 seconds.
     * @param projectId The id of the project
     */
    public ProjectRefresh(int projectId) {
        this.projectId = projectId;
        this.usersSeenRefresh = new ArrayList<>();
        this.time = System.currentTimeMillis();
    }

    /**
     * Returns true if the refresh is more than 5 seconds old.
     * @return True if the refresh has timed out
     */
    public boolean hasTimedOut() {
        return System.currentTimeMillis() - time > 5000;
    }

    /**
     * Refreshes are relevant if they are about the same project, and the user has not already been notified.
     * @param projectId The id of the project
     * @param userId The id of the user viewing the project
     * @return True if the refresh is relevant
     */
    public boolean isRelevant(int projectId, int userId) {
        return this.projectId == projectId && !usersSeenRefresh.contains(userId);
    }

    /**
     * Used to mark that a user has been notified about this refresh.
     * Does NOT notify the user itself.
     * @param userId The id of the user viewing the project
     */
    public void notifyUser(int userId) {
        usersSeenRefresh.add(userId);
    }

}
