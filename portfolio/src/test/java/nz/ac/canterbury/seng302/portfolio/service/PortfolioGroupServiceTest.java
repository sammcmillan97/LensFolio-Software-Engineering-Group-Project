package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroupRepository;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

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
        List<PortfolioGroup> groups = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, groups.size());
        groupService.getGroupById(5);
        groups = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(2, groups.size());
    }

    //Test that querying a group which does exist does not create that group.
    @Test
    void whenGroupExists_TestGroupNotCreatedOnQuery() {
        groupService.getGroupById(3);
        List<PortfolioGroup> groups = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, groups.size());
        groupService.getGroupById(3);
        groups = (List<PortfolioGroup>) groupRepository.findAll();
        assertEquals(1, groups.size());
    }

    // Test that setting all parameters at once updates them all
    @Test
    void updateAllGroupParametersTest() {
        // Create the group
        String testName = "test name";
        String testApiKey = "API KEY";
        String testRepoId = "1234";
        String testServerUrl = "https://server.com";
        groupService.getGroupById(3);
        groupService.updateRepositoryInformation(3, testName, testApiKey, testRepoId, testServerUrl);
        PortfolioGroup group = groupService.getGroupById(3);
        assertEquals(testName, group.getRepositoryName());
        assertEquals(testApiKey, group.getGitlabAccessToken());
        assertEquals(testRepoId, group.getGitlabProjectId());
        assertEquals(testServerUrl, group.getGitlabServerUrl());
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

    //Test that getting the default gitlab project id works (should be null)
    @Test
    void getGitlabProjectIdTest() {
        String resultProjectId = groupService.getGitlabProjectId(3);
        assertNull(resultProjectId);
    }

    //Test that setting the gitlab project id works properly
    @Test
    void setGitlabProjectIdTest() {
        String testProjectId = "2";
        groupService.setGitlabProjectId(3, testProjectId);
        String resultProjectId = groupService.getGitlabProjectId(3);
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

    // Test that deleting the repository when it doesn't exist works as expected
    @Test
    void whenRepositoryDoesntExist_testDeleteRepository() {
        assertFalse(groupRepository.existsById(1234));
        try {
            groupService.deletePortfolioGroupById(1234);
        } catch (EmptyResultDataAccessException e) {
            String expectedMessage = "No class nz.ac.canterbury.seng302.portfolio.model.PortfolioGroup entity with id 1234 exists!";
            assertEquals(expectedMessage, e.getMessage());
        }

        assertFalse(groupRepository.existsById(1234));
    }

    // Test that deleting the repository when it exists works as expected
    @Test
    void whenRepositoryExists_testDeleteRepository() {
        // Make sure the group exists
        groupService.getGroupById(1);
        List<PortfolioGroup> groups = (List<PortfolioGroup>) groupRepository.findAll();
        int id = groups.get(0).getId();
        assertTrue(groupRepository.existsById(id));
        
        groupService.deletePortfolioGroupById(id);
        assertFalse(groupRepository.existsById(id));
    }

}
