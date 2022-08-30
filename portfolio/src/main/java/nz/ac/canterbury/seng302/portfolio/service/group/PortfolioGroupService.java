package nz.ac.canterbury.seng302.portfolio.service.group;

import nz.ac.canterbury.seng302.portfolio.model.group.PortfolioGroup;
import nz.ac.canterbury.seng302.portfolio.repository.group.PortfolioGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioGroupService {
    @Autowired
    private GroupsClientService groupsClientService;
    @Autowired
    private PortfolioGroupRepository portfolioGroupRepository;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Creates a portfolio group with the given group id and parent project id
     * @param groupId Group id from the idp
     * @param parentProjectId Project id that the group belongs to
     */
    public void createPortfolioGroup(int groupId, int parentProjectId) {
        PortfolioGroup portfolioGroup = new PortfolioGroup(groupId, parentProjectId);
        portfolioGroupRepository.save(portfolioGroup);
        String message = "New portfolio group created with group id " + groupId + " and parent project id " + parentProjectId;
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Deletes the portfolioGroup with the given group id
     * @param groupId Group id from the idp
     */
    public void deletePortfolioGroupByGroupId(int groupId) {
        try {
            portfolioGroupRepository.deleteByGroupId(groupId);
            String message = "Group " + groupId + " portfolio group deleted successfully";
            PORTFOLIO_LOGGER.info(message);
        } catch (Exception e) {
            String message = "Group " + groupId + " portfolio group could not be deleted";
            PORTFOLIO_LOGGER.error(message);
        }
    }

    /**
     * Returns a list of portfolio groups that have the given parent project
     * @param parentProjectId Id of the required parent project
     * @return A list of portfolio groups with the given parent project
     */
    public List<PortfolioGroup> findPortfolioGroupsByParentProjectId(int parentProjectId) {
        return portfolioGroupRepository.findByParentProjectId(parentProjectId);
    }
}
