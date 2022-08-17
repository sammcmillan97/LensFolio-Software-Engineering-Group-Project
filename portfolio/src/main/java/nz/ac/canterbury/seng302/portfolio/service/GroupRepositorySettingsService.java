package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GroupRepositorySettings;
import nz.ac.canterbury.seng302.portfolio.repository.GroupRepositorySettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupRepositorySettingsService {

    @Autowired
    private GroupRepositorySettingsRepository groupRepository;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Gets a group by its id. Creates a default group with that id if it doesn't exist
     * @param groupId The group's id from the identity provider
     * @return The group's portfolio information
     */
    public GroupRepositorySettings getGroupRepositorySettingsByGroupId(int groupId) {
        GroupRepositorySettings group;
        group = groupRepository.findByGroupId(groupId);

        // If portfolio group doesn't exist, create a new one
        if (group == null) {
            group = new GroupRepositorySettings(groupId);
            groupRepository.save(group);
        }
        return group;
    }

    /**
     * Gets the group's current gitlab server url. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return The group's current gitlab server url
     */
    public String getGitlabServerUrl(int groupId) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        return group.getGitlabServerUrl();
    }

    /**
     * Sets the group's gitlab server url. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabServerUrl The group's new gitlab server url
     */
    public void setGitlabServerUrl(int groupId, String gitlabServerUrl) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        group.setGitlabServerUrl(gitlabServerUrl);
        groupRepository.save(group);
        String message = "Group " + groupId + " repository server url updated successfully";
        PORTFOLIO_LOGGER.error(message);
    }

    /**
     * Gets the group's current gitlab project id. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return The group's current gitlab project id
     */
    public String getGitlabProjectId(int groupId) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        return group.getGitlabProjectId();
    }

    /**
     * Sets the group's gitlab project id. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabProjectId The group's new gitlab project id
     */
    public void setGitlabProjectId(int groupId, String gitlabProjectId) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        group.setGitlabProjectId(gitlabProjectId);
        groupRepository.save(group);
        String message = "Group " + groupId + " repository id updated successfully";
        PORTFOLIO_LOGGER.error(message);
    }

    /**
     * Gets the group's current gitlab access token. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return The group's current gitlab access token
     */
    public String getGitlabAccessToken(int groupId) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        return group.getGitlabAccessToken();
    }

    /**
     * Sets the group's gitlab access token. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param gitlabAccessToken The group's new gitlab access token
     */
    public void setGitlabAccessToken(int groupId, String gitlabAccessToken) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        group.setGitlabAccessToken(gitlabAccessToken);
        groupRepository.save(group);
        String message = "Group " + groupId + " repository access token updated successfully";
        PORTFOLIO_LOGGER.error(message);
    }

    /**
     * Gets the group's current repository name. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @return The group's current repository name
     */
    public String getRepositoryName(int groupId) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        return group.getRepositoryName();
    }

    /**
     * Sets the group's repository name. This is for the group settings page
     * @param groupId The group's id from the identity provider
     * @param repositoryName The group's new repository name
     */
    public void setRepositoryName(int groupId, String repositoryName) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        group.setRepositoryName(repositoryName);
        groupRepository.save(group);
        String message = "Group " + groupId + " repository name updated successfully";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Updates all of the group's repository information with the provided parameters
     * @param groupId The group's id from the identity provider
     * @param repositoryName The group's new repository name
     * @param gitlabAccessToken The group's new gitlab access token
     * @param gitlabProjectId The group's new gitlab project id
     * @param gitlabServerUrl The group's current gitlab server url
     */
    public void updateRepositoryInformation(int groupId, String repositoryName, String gitlabAccessToken, String gitlabProjectId, String gitlabServerUrl) {
        GroupRepositorySettings group = getGroupRepositorySettingsByGroupId(groupId);
        group.setRepositoryName(repositoryName);
        group.setGitlabAccessToken(gitlabAccessToken);
        group.setGitlabProjectId(gitlabProjectId);
        group.setGitlabServerUrl(gitlabServerUrl);
        groupRepository.save(group);
        String message = "Group " + groupId + " settings updated successfully";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Deletes the given groupRepositorySettings
     * @param groupId The group's id from the identity provider
     */
    public void deleteGroupRepositoryByGroupId(int groupId) {
        if (groupRepository.existsByGroupId(groupId)) {
            GroupRepositorySettings group = groupRepository.findByGroupId(groupId);
            groupRepository.delete(group);
        }
        String message = "Group " + groupId + " settings deleted successfully";
        PORTFOLIO_LOGGER.info(message);
    }

    /**
     * Checks if group repository settings exist for the given group id
     * @param groupId the group id to check
     * @return Boolean saying whether the group repository settings for that group exist
     */
    public boolean existsByGroupId(int groupId) {
        return groupRepository.existsByGroupId(groupId);
    }
}
