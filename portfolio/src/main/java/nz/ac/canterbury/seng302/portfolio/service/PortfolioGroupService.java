package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioGroupService {

    @Autowired
    private PortfolioGroupRepository groupRepository;

    /**
     * Gets a group by its id. Creates a default group with that id if it doesn't exist
     * @param groupId The group's id from the identity provider
     * @return The group's portfolio information
     */
    public PortfolioGroup getGroupById(int groupId) {
        PortfolioGroup group;
        group = groupRepository.findByGroupId(groupId);

        // If portfolio group doesn't exist, create a new one
        if (group == null) {
            group = new PortfolioGroup(groupId);
            groupRepository.save(group);
        }
        return group;
    }

    /**
     * Gets the group's current gitlab server url. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return the group's current gitlab server url
     */
    public String getGitlabServerUrl(int groupId) {
        PortfolioGroup group = getGroupById(groupId);
        return group.getGitlabServerUrl();
    }

    /**
     * Sets the group's gitlab server url. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabServerUrl the group's new gitlab server url
     */
    public void setGitlabServerUrl(int groupId, String gitlabServerUrl) {
        PortfolioGroup group = getGroupById(groupId);
        group.setGitlabServerUrl(gitlabServerUrl);
        groupRepository.save(group);
    }

    /**
     * Gets the group's current gitlab project id. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return the group's current gitlab project id
     */
    public int getGitlabProjectId(int groupId) {
        PortfolioGroup group = getGroupById(groupId);
        return group.getGitlabProjectId();
    }

    /**
     * Sets the group's gitlab project id. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabProjectId the group's new gitlab project id
     */
    public void setGitlabProjectId(int groupId, int gitlabProjectId) {
        PortfolioGroup group = getGroupById(groupId);
        group.setGitlabProjectId(gitlabProjectId);
        groupRepository.save(group);
    }

    /**
     * Gets the group's current gitlab access token. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return the group's current gitlab access token
     */
    public String getGitlabAccessToken(int groupId) {
        PortfolioGroup group = getGroupById(groupId);
        return group.getGitlabAccessToken();
    }

    /**
     * Sets the group's gitlab access token. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabAccessToken the group's new gitlab access token
     */
    public void setGitlabAccessToken(int groupId, String gitlabAccessToken) {
        PortfolioGroup group = getGroupById(groupId);
        group.setGitlabAccessToken(gitlabAccessToken);
        groupRepository.save(group);
    }
}
