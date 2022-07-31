package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

/**
 * Represents a group of the portfolio.
 * Only contains information specific to the portfolio, for general group information see the Group class.
 */
@Entity
@Table(name="PORTFOLIO_GROUP")
public class PortfolioGroup {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private int groupId;

    private int currentProject = 1;
    private String gitlabServerUrl = "https://eng-git.canterbury.ac.nz";
    private String gitlabProjectId = null;
    private String gitlabAccessToken = null;
    private String repositoryName = "";

    /**
     * Create a portfolio group
     * @param groupId Should be the same as the group's id from the identity provider
     * @param gitlabServerUrl The group's gitlab server url
     * @param gitlabProjectId The group's gitlab project id
     * @param gitlabAccessToken The group's gitlab access token
     */
    public PortfolioGroup(int groupId, String gitlabServerUrl, String gitlabProjectId, String gitlabAccessToken) {
        this.groupId = groupId;
        this.gitlabServerUrl = gitlabServerUrl;
        this.gitlabProjectId = gitlabProjectId;
    }

    /**
     * Create a portfolio group
     * @param groupId Should be the same as the group's id from the identity provider
     * @param gitlabServerUrl The group's gitlab server url
     * @param gitlabProjectId The group's gitlab project id
     * @param gitlabAccessToken The group's gitlab access token
     */
    public PortfolioGroup(int groupId, String gitlabServerUrl, String gitlabProjectId, String gitlabAccessToken, String repositoryName) {
        this.groupId = groupId;
        this.gitlabServerUrl = gitlabServerUrl;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabAccessToken = gitlabAccessToken;
        this.repositoryName = repositoryName;
    }

    public PortfolioGroup(int groupId) {
        this.groupId = groupId;
    }

    protected PortfolioGroup() {

    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGitlabServerUrl() {
        return gitlabServerUrl;
    }

    public void setGitlabServerUrl(String gitlabServerUrl) {
        this.gitlabServerUrl = gitlabServerUrl;
    }

    public String getGitlabProjectId() {
        return gitlabProjectId;
    }

    public void setGitlabProjectId(String gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
    }

    public String getGitlabAccessToken() {
        return gitlabAccessToken;
    }

    public void setGitlabAccessToken(String gitlabAccessToken) {
        this.gitlabAccessToken = gitlabAccessToken;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public int getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(int currentProject) {
        this.currentProject = currentProject;
    }
}
