package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.ProjectEdit;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRefresh;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a list of edits being made by a variety of users to a variety of projects.
 */
@Service
public class ProjectEdits {

    private final List<ProjectEdit> projectEditList;

    private final List<ProjectRefresh> projectRefreshList;


    /**
     * Default constructor, initialises the edit list and refresh list.
     */
    public ProjectEdits() {
        projectEditList = new ArrayList<>();
        projectRefreshList = new ArrayList<>();
    }

    /**
     * Gets a list of edits to send to the frontend.
     * The edits should relate to the project the user is viewing, but not be by them.
     * It is needed to remove all timed out edits first.
     * Also sends a single parameter 'refresh' to the user telling them if they need to refresh their page.
     * This is if the page has changed since the user last called this method.
     * @param projectId The id of the project
     * @param userId The id of the user viewing the project
     * @return A JSON string to send to the frontend representing the edits a user is interested in,
     * plus the refresh parameter. For example:
     * {
     *     "edits": ["Fabian is editing Awesome Project", "Moffat is editing Awesome Project"],
     *     "refresh": false
     * }
     */
    public String getEdits(int projectId, int userId) {
        projectEditList.removeIf(ProjectEdit::hasTimedOut);
        projectRefreshList.removeIf(ProjectRefresh::hasTimedOut);
        StringBuilder result = new StringBuilder("{\"edits\": [");
        boolean firstEdit = true;
        for (ProjectEdit edit : projectEditList) {
            if (edit.isRelevant(projectId, userId)) {
                if (!firstEdit) {
                    result.append(",");
                } else {
                    firstEdit = false;
                }
                result.append("\"").append(edit).append("\"");
            }
        }
        result.append("], \"refresh\":");
        // Append refresh:true to tell the user to refresh if any refreshes are relevant.
        // Else append refresh:false.
        String refreshString = "false";
        for (ProjectRefresh refresh: projectRefreshList) {
            if (refresh.isRelevant(projectId, userId)) {
                refreshString = "true";
                // Once the user has been told to refresh once, they don't need to do it again.
                // Calling this method means they won't be told twice
                refresh.notifyUser(userId);
            }
        }
        result.append(refreshString);
        result.append("}");
        return result.toString();
    }

    /**
     * Create a new project edit. It is linked to a specific project and user.
     * It has a message which will show to other users that this user is editing the page.
     * It will time out after 5 seconds.
     * It is necessary to remove any old edits the same user may have made.
     * @param projectId The id of the project
     * @param userId The id of the user editing the project
     * @param editString The string to display to other users representing the edit.
     */
    public void newEdit(int projectId, int userId, String editString) {
        projectEditList.removeIf(edit -> edit.isFromUser(userId));
        projectEditList.add(new ProjectEdit(projectId, userId, editString));
    }

    /**
     * Create a new project refresh. It is linked to a specific project.
     * It will time out after 5 seconds.
     * @param projectId The id of the project
     */
    public void refreshProject(int projectId) {
        projectRefreshList.add(new ProjectRefresh(projectId));
    }

}
