package nz.ac.canterbury.seng302.portfolio.controller.group;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupChartDataService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupsClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Controller
public class GroupChartDataController {
    @Autowired
    private GroupsClientService groupsClientService;
    @Autowired
    private GroupChartDataService groupChartDataService;

    /**
     * Used by the front end to fetch the number of commits for each category
     * @param groupId The id of the group that data is requested for
     * @return A map of category names to the number of times they're used
     */
    @GetMapping("/group-{groupId}-categoriesData")
    public @ResponseBody Map<String, Integer> getCategoriesData(@PathVariable int groupId) {
        Group group = new Group(groupsClientService.getGroupDetailsById(groupId));
        return groupChartDataService.getGroupCategoryInfo(group);
    }
}
