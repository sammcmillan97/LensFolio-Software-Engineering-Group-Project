package nz.ac.canterbury.seng302.portfolio.service.group;

import nz.ac.canterbury.seng302.portfolio.model.group.PortfolioGroup;
import nz.ac.canterbury.seng302.portfolio.repository.group.PortfolioGroupRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class PortfolioGroupService {
    @Autowired
    private PortfolioGroupRepository portfolioGroupRepository;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Creates a portfolio group with the given group id and parent project id
     * @param groupId Group id from the idp
     * @param parentProjectId Project id that the group belongs to
     */
    public boolean createPortfolioGroup(int groupId, int parentProjectId) {
        PortfolioGroup portfolioGroup = new PortfolioGroup(groupId, parentProjectId);
        if (!portfolioGroupRepository.existsByGroupId(groupId)) {
            portfolioGroupRepository.save(portfolioGroup);
            String message = "New portfolio group created with group id " + groupId + " and parent project id " + parentProjectId;
            PORTFOLIO_LOGGER.info(message);
            return true;
        } else {
            String message = "Could not create new portfolio group with group id " + groupId + ". Group id already exists in database";
            PORTFOLIO_LOGGER.error(message);
            return false;
        }
    }

    /**
     * Fetches a portfolio group with the given group id
     * @param groupId the group id of the portfolio group
     * @return the portfolio group object
     */
    public PortfolioGroup getPortfolioGroupByGroupId(int groupId) {
        Optional<PortfolioGroup> portfolioGroup = portfolioGroupRepository.findByGroupId(groupId);
        if (portfolioGroup.isPresent()) {
            return portfolioGroup.get();
        } else {
            String message = "Portfolio group with group id " + groupId + " does not exist";
            PORTFOLIO_LOGGER.error(message);
            throw new NoSuchElementException(message);
        }
    }

    /**
     * Deletes the portfolioGroup with the given group id
     * @param groupId Group id from the idp
     */
    public void deletePortfolioGroupByGroupId(int groupId) {
        if (portfolioGroupRepository.existsByGroupId(groupId)) {
            PortfolioGroup g = getPortfolioGroupByGroupId(groupId);
            portfolioGroupRepository.delete(g);
            String message = "Group " + groupId + " portfolio group deleted successfully";
            PORTFOLIO_LOGGER.info(message);
        } else {
            String message = "Group " + groupId + " portfolio group could not be deleted because it does not exist in the database.";
            PORTFOLIO_LOGGER.error(message);
            throw new NoSuchElementException(message);
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

    /**
     * Fetches the parent project id of the given group
     * @param groupId Group id from the idp
     * @return The id of the groups parent project
     */
    public int findParentProjectIdByGroupId(int groupId) {
        return getPortfolioGroupByGroupId(groupId).getParentProjectId();
    }
}
