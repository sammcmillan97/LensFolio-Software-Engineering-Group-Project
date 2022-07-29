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

    private int currentProject;
    private String gitlabServerUrl;
    private int gitlabProjectId;
    private String gitlabAccessToken;

    /**
     * Create a portfolio group
     * @param groupId Should be the same as the group's id from the identity provider
     * @param gitlabServerUrl The group's gitlab server url
     * @param gitlabProjectId The group's gitlab project id
     * @param gitlabAccessToken The group's gitlab access token
     */
    public PortfolioGroup(int groupId, String gitlabServerUrl, int gitlabProjectId, String gitlabAccessToken) {
        this.groupId = groupId;
        this.gitlabServerUrl = gitlabServerUrl;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabAccessToken = gitlabAccessToken;
        this.currentProject = 1;
    }

    public PortfolioGroup(int groupId) {
        this.groupId = groupId;
        this.currentProject = 1;
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

    public int getGitlabProjectId() {
        return gitlabProjectId;
    }

    public void setGitlabProjectId(int gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
    }

    public String getGitlabAccessToken() {
        return gitlabAccessToken;
    }

    public void setGitlabAccessToken(String gitlabAccessToken) {
        this.gitlabAccessToken = gitlabAccessToken;
    }

    public int getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(int currentProject) {
        this.currentProject = currentProject;
    }
}
