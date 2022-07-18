package nz.ac.canterbury.seng302.portfolio.model;

/**
 * Class which represents an edit currently being made to a project.
 */
public class ProjectEdit {

    private final int projectId;
    private final int userId;
    private final String editString;
    private final long time;

    /**
     * Create a new project edit. It is linked to a specific project and user.
     * It has a message which will show to other users that this user is editing the page.
     * It will time out after 5 seconds.
     * @param projectId The id of the project
     * @param userId The id of the user editing the project
     * @param editString The string to display to other users representing the edit.
     */
    public ProjectEdit(int projectId, int userId, String editString) {
        this.projectId = projectId;
        this.userId = userId;
        this.editString = editString;
        this.time = System.currentTimeMillis();
    }

    /**
     * Returns true if the edit is more than 5 seconds old.
     * Users editing a page should continuously update the server more than every 5 seconds.
     * @return True if the edit has timed out
     */
    public boolean hasTimedOut() {
        return System.currentTimeMillis() - time > 5000;
    }

    /**
     * Edits are relevant if they are about the same project, and NOT the same user.
     * User's don't need to be notified about their own edits.
     * @param projectId The id of the project
     * @param userId The id of the user viewing the project
     * @return True if the edit is relevant
     */
    public boolean isRelevant(int projectId, int userId) {
        return this.projectId == projectId && this.userId != userId;
    }

    /**
     * Checks if an edit was made by a specific user.
     * @param userId The id of the user
     * @return True if edit was from this user
     */
    public boolean isFromUser(int userId) {
        return this.userId == userId;
    }

    /**
     * Checks if an edit was made by on a specific project.
     * @param projectId The id of the user
     * @return True if edit was from this project
     */
    public boolean isFromProject(int projectId) {
        return this.projectId == projectId;
    }

    /**
     * Simply return the edit string.
     * @return The string representing the edit
     */
    public String toString() {
        return editString;
    }

}
