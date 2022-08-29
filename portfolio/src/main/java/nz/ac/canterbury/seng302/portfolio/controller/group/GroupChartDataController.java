package nz.ac.canterbury.seng302.portfolio.controller.group;

import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.project.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupChartDataService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupsClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
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

    @GetMapping("/group-{groupId}-categoriesData")
    public @ResponseBody Map<String, Integer> getCategoriesData(@PathVariable int groupId) {
        Group group = new Group(groupsClientService.getGroupDetailsById(groupId));
        return groupChartDataService.getGroupCategoryInfo(group);
    }
}
