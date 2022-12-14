package nz.ac.canterbury.seng302.portfolio.service.group;

import com.google.common.annotations.VisibleForTesting;
import nz.ac.canterbury.seng302.portfolio.model.group.GroupRepositorySettings;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GitlabConnectionService {
    @Autowired
    GroupRepositorySettingsService groupRepositorySettingsService;
    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Fetches the given group's repository information
     * @param groupId The group id to get commits from
     * @return A GroupRepositorySettings object containing the group's repository information
     * @throws NoSuchFieldException If the given group doesn't have any repository settings
     */
    @VisibleForTesting
    protected GroupRepositorySettings getGroupRepositorySettings(int groupId) throws NoSuchFieldException {
        if (!groupRepositorySettingsService.existsByGroupId(groupId)) {
            String message = "Group " + groupId + " doesn't have any repository settings";
            PORTFOLIO_LOGGER.error(message);
            throw new NoSuchFieldException("Given group id doesn't have any repository settings");
        }
        return groupRepositorySettingsService.getGroupRepositorySettingsByGroupId(groupId);
    }

    /**
     * Connects to the given group's repository
     * @param groupId The group id to connect to
     * @return A GitLabApi connection to the given groups repository
     * @throws NoSuchFieldException If the given group doesn't have any repository settings
     */
    @VisibleForTesting
    protected GitLabApi getGitLabApiConnection(int groupId) throws NoSuchFieldException {
        GroupRepositorySettings repositorySettings = getGroupRepositorySettings(groupId);

        return new GitLabApi(repositorySettings.getGitlabServerUrl(), repositorySettings.getGitlabAccessToken());
    }

    /**
     * Attempts to create a connection to the gitlab repository in the group settings
     * then fetch a list of commits from that repository.
     * @param groupId The group id to get commits from
     * @return A list of all commits from the repository
     * @throws GitLabApiException If it can't connect to the project
     * @throws NoSuchFieldException If the given group doesn't have any repository settings
     */
    public List<Commit> getAllCommits(int groupId) throws GitLabApiException, NoSuchFieldException {
        GroupRepositorySettings repositorySettings = getGroupRepositorySettings(groupId);
        GitLabApi gitLabApiConnection = getGitLabApiConnection(groupId);
        return gitLabApiConnection.getCommitsApi().getCommits(repositorySettings.getGitlabProjectId());
    }
}
