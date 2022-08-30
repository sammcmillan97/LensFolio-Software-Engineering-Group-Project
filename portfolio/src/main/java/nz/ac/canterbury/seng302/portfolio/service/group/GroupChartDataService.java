package nz.ac.canterbury.seng302.portfolio.service.group;

import com.google.common.annotations.VisibleForTesting;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Categories;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupsServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupChartDataService {
    @Autowired
    private EvidenceService evidenceService;

    /**
     * Iterates through the groups members then the members' pieces of evidence
     * to count up the occurrences of each category
     * @param group the group object that the data is wanted for
     * @return A map of categories to their occurrences in the group's pieces of evidence
     */
    public Map<String, Integer> getGroupCategoryInfo(Group group) {
        int parentProjectId = group.getParentProject();

        // Initialise the hashmap to have a count of 0 for every category
        Map<String, Integer> categoryCounts = new HashMap<>();
        categoryCounts.put("Service", 0);
        categoryCounts.put("Quantitative", 0);
        categoryCounts.put("Qualitative", 0);

        // Iterate through every user in the group
        for (User user : group.getMembers()) {
            // Iterate through all of that user's evidence for the groups project
            for (Evidence e : evidenceService.getEvidenceForPortfolio(user.getId(), parentProjectId)) {

                // Increase the count of the category by 1 if the piece of evidence has that category
                if (e.getCategories().contains(Categories.SERVICE)) {
                    categoryCounts.merge("Service", 1, Integer::sum);
                }
                if (e.getCategories().contains(Categories.QUANTITATIVE)) {
                    categoryCounts.merge("Quantitative", 1, Integer::sum);
                }
                if (e.getCategories().contains(Categories.QUALITATIVE)) {
                    categoryCounts.merge("Qualitative", 1, Integer::sum);
                }
            }
        }
        return categoryCounts;
    }

    /**
     * Only for mocking purposes
     * Updates the current EvidenceService with a new one
     * @param newEvidenceService the new (mocked) EvidenceService
     */
    @VisibleForTesting
    protected void setEvidenceService(EvidenceService newEvidenceService) {
        evidenceService = newEvidenceService;
    }

    /**
     * Only for mocking purposes
     * @return the current evidence service
     */
    @VisibleForTesting
    protected EvidenceService getEvidenceService() {
        return evidenceService;
    }
}
