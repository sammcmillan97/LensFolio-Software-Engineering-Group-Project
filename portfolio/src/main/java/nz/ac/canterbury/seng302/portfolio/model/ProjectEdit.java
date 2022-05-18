package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectEdit {

    @Autowired
    UserAccountClientService userAccountClientService;

    @Autowired
    ProjectService projectService;

    private final int projectId;
    private final int userId;
    private final long time;

    public ProjectEdit(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
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
        return "'" + userAccountClientService.getUserAccountById(userId).getFirstName() +
                " is editing " + projectService.getProjectById(projectId).getName() + "'";
    }

}
