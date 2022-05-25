package nz.ac.canterbury.seng302.portfolio.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a list of edits being made by a variety of users to a variety of projects.
 */
public class ProjectEdits {

    private final List<ProjectEdit> projectEditList;

    private final List<ProjectRefresh> projectRefreshList;


    /**
     * Default constructor, initialises the edit list.
     */
    public ProjectEdits() {
        projectEditList = new ArrayList<>();
        projectRefreshList = new ArrayList<>();
    }

    /**
     * Gets a list of edits to send to the frontend.
     * The edits should relate to the project the user is viewing, but not be by them.
     * It is needed to remove all timed out edits first.
     * @param projectId The id of the project
     * @param userId The id of the user viewing the project
     * @return A JSON string to send to the frontend representing the edits a user is interested in.
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
        result.append("]}");
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

}
