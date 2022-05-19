package nz.ac.canterbury.seng302.portfolio.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectEdits {

    private final List<ProjectEdit> projectEditList;

    public ProjectEdits() {
        projectEditList = new ArrayList<>();
    }

    public String getEdits(int projectId, int userId) {
        projectEditList.removeIf(ProjectEdit::hasTimedOut);
        StringBuilder result = new StringBuilder("{'edits': [");
        boolean firstEdit = true;
        for (ProjectEdit edit : projectEditList) {
            if (edit.isRelevant(projectId, userId)) {
                if (!firstEdit) {
                    result.append(",");
                } else {
                    firstEdit = false;
                }
                result.append("'").append(edit).append("'");
            }
        }
        result.append("]}");
        return result.toString();
    }

    public void newEdit(int projectId, int userId, String editString) {
        projectEditList.removeIf(edit -> edit.isFromUser(userId));
        projectEditList.add(new ProjectEdit(projectId, userId, editString));
    }

}
