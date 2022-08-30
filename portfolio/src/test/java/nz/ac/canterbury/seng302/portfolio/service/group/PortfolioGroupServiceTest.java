package nz.ac.canterbury.seng302.portfolio.service.group;

import nz.ac.canterbury.seng302.portfolio.model.group.PortfolioGroup;
import nz.ac.canterbury.seng302.portfolio.repository.group.PortfolioGroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class PortfolioGroupServiceTest {
    @Autowired
    private PortfolioGroupService portfolioGroupService;
    @Autowired
    private PortfolioGroupRepository portfolioGroupRepository;
    int testProjectId1 = 1;
    int testProjectId2 = 2;
    int testGroupId1 = 1;
    int testGroupId2 = 2;

    @Test
    @Transactional
    void whenNoPortfolioGroupsExist_testCreatePortfolioGroup() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
    }

    @Test
    @Transactional
    void whenOnePortfolioGroupExists_testCreatePortfolioGroupWithSameGroupId() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        assertTrue(portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1));
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        assertFalse(portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1));
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
    }

    @Test
    @Transactional
    void whenNoPortfolioGroupsExist_testGetPortfolioGroupById() {
        assertThrows(NoSuchElementException.class, () -> portfolioGroupService.getPortfolioGroupByGroupId(testGroupId1), "Portfolio group with group id " + testGroupId1 + " does not exist");
    }

    @Test
    @Transactional
    void whenOnePortfolioGroupExists_testGetPortfolioGroupById() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());

        PortfolioGroup portfolioGroup = portfolioGroupService.getPortfolioGroupByGroupId(testGroupId1);
        assertEquals(testGroupId1, portfolioGroup.getGroupId());
        assertEquals(testProjectId1, portfolioGroup.getParentProjectId());
    }

    @Test
    @Transactional
    void whenNoPortfolioGroupsExist_testDeletePortfolioGroupById() {
        assertThrows(NoSuchElementException.class, () -> portfolioGroupService.deletePortfolioGroupByGroupId(testGroupId1), "Group " + testGroupId1 + " portfolio group could not be deleted because it does not exist in the database.");
    }

    @Test
    @Transactional
    void whenOnePortfolioGroupExists_testDeletePortfolioGroupById() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());

        portfolioGroupService.deletePortfolioGroupByGroupId(testGroupId1);
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
    }

    @Test
    @Transactional
    void whenNoPortfolioGroupsExist_testFindPortfolioGroupsByParentProjectId() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        assertEquals(0, portfolioGroupService.findPortfolioGroupsByParentProjectId(testProjectId1).size());
    }

    @Test
    @Transactional
    void whenOnePortfolioGroupExist_testFindPortfolioGroupsByParentProjectId() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        assertEquals(1, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());

        assertEquals(1, portfolioGroupService.findPortfolioGroupsByParentProjectId(testProjectId1).size());
    }

    @Test
    @Transactional
    void whenTwoPortfolioGroupsExistWithSameParentProject_testFindPortfolioGroupsByParentProjectId() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        portfolioGroupService.createPortfolioGroup(testGroupId2, testProjectId1);
        assertEquals(2, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());

        assertEquals(2, portfolioGroupService.findPortfolioGroupsByParentProjectId(testProjectId1).size());
    }

    @Test
    @Transactional
    void whenTwoPortfolioGroupsExistWithDifferentParentProject_testFindPortfolioGroupsByParentProjectId() {
        assertEquals(0, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());
        portfolioGroupService.createPortfolioGroup(testGroupId1, testProjectId1);
        portfolioGroupService.createPortfolioGroup(testGroupId2, testProjectId2);
        assertEquals(2, ((List<PortfolioGroup>)portfolioGroupRepository.findAll()).size());

        assertEquals(1, portfolioGroupService.findPortfolioGroupsByParentProjectId(testProjectId1).size());
        assertEquals(1, portfolioGroupService.findPortfolioGroupsByParentProjectId(testProjectId2).size());
    }
}
