package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroupRepository;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class PortfolioGroupServiceTest {

    @Autowired
    PortfolioGroupService groupService;

    @Autowired
    PortfolioGroupRepository groupRepository;

    @BeforeEach
    void cleanDatabase() {
        groupRepository.deleteAll();
    }

    //Test that querying a group which does not exist creates that group.
    @Test
    void whenGroupDoesntExist_TestGroupCreatedOnQuery() {
        groupService.getGroupById(3);
        List<PortfolioGroup> users = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, users.size());
        groupService.getGroupById(5);
        users = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(2, users.size());
    }

    //Test that querying a group which does exist does not create that group.
    @Test
    void whenGroupExists_TestGroupNotCreatedOnQuery() {
        groupService.getGroupById(3);
        List<PortfolioGroup> users = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, users.size());
        groupService.getGroupById(3);
        users = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, users.size());
    }

    //Test that getting the default gitlab server url works (should be "https://eng-git.canterbury.ac.nz")
    @Test
    void getGitlabServerUrlTest() {
        String resultServerUrl = groupService.getGitlabServerUrl(3);
        assertEquals("https://eng-git.canterbury.ac.nz", resultServerUrl);
    }

    //Test that setting the gitlab server url works properly
    @Test
    void setGitlabServerUrlTest() {
        String testServerUrl = "https://eng-git.canterbury.ac.nz";
        groupService.setGitlabServerUrl(3, testServerUrl);
        String resultServerUrl = groupService.getGitlabServerUrl(3);
        assertEquals(testServerUrl, resultServerUrl);
    }

    //Test that getting the default gitlab project id works (should be -1)
    @Test
    void getGitlabProjectIdTest() {
        int resultProjectId = groupService.getGitlabProjectId(3);
        assertEquals(-1, resultProjectId);
    }

    //Test that setting the gitlab project id works properly
    @Test
    void setGitlabProjectIdTest() {
        int testProjectId = 2;
        groupService.setGitlabProjectId(3, testProjectId);
        int resultProjectId = groupService.getGitlabProjectId(3);
        assertEquals(testProjectId, resultProjectId);
    }

    //Test that getting the default gitlab access token works (should be null)
    @Test
    void getGitlabAccessTokenTest() {
        String resultAccessToken = groupService.getGitlabAccessToken(3);
        assertNull(resultAccessToken);
    }

    //Test that setting the gitlab access token works properly
    @Test
    void setGitlabAccessTokenTest() {
        String testAccessToken = "randomAccessToken";
        groupService.setGitlabAccessToken(3, testAccessToken);
        String resultAccessToken = groupService.getGitlabAccessToken(3);
        assertEquals(testAccessToken, resultAccessToken);
    }

    //Test that getting the default repository name works (should be empty string)
    @Test
    void getRepositoryNameTest() {
        String resultRepositoryName = groupService.getRepositoryName(3);
        assertEquals("", resultRepositoryName);
    }

    //Test that setting the repository name works properly
    @Test
    void setRepositoryNameTest() {
        String testRepositoryName = "randomAccessToken";
        groupService.setRepositoryName(3, testRepositoryName);
        String resultRepositoryName = groupService.getRepositoryName(3);
        assertEquals(testRepositoryName, resultRepositoryName);
    }

}
