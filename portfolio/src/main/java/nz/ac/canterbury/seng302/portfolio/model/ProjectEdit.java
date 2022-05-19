package nz.ac.canterbury.seng302.portfolio.model;

public class ProjectEdit {

    private final int projectId;
    private final int userId;
    private String editString;
    private final long time;

    public ProjectEdit(int projectId, int userId, String editString) {
        this.projectId = projectId;
        this.userId = userId;
        this.editString = editString;
        this.time = System.currentTimeMillis();
    }

    public boolean hasTimedOut() {
        return System.currentTimeMillis() - time > 5000;
    }

    public boolean isRelevant(int projectId, int userId) {
        return this.projectId == projectId && this.userId != userId;
    }

    public boolean isFromUser(int userId) {
        return this.userId == userId;
    }

    public String toString() {
        return editString;
    }

}
