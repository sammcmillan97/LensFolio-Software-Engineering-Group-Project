package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioUserRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
class PortfolioUserServiceTest {

    @Autowired
    PortfolioUserService service;

    @Autowired
    PortfolioUserRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    //Test that querying a user which does not exist creates that user.
    @Test
    void queryNewUserTest() {
        service.getUserById(3);
        List<PortfolioUser> users = (List<PortfolioUser>) repository.findAll();
        assertEquals(1, users.size());
        service.getUserById(5);
        users = (List<PortfolioUser>) repository.findAll();
        assertEquals(2, users.size());
    }

    //Test that querying a user which does exist does not create that user.
    @Test
    void givenAUserHasBeenRemoved_queryUser() {
        service.getUserById(3);
        List<PortfolioUser> users = (List<PortfolioUser>) repository.findAll();
        assertEquals(1, users.size());
        service.getUserById(3);
        users = (List<PortfolioUser>) repository.findAll();
        assertEquals(1, users.size());
    }

    //Test that getting the default user list sort type works (should be by name)
    @Test
    void givenDefaultSortType_getUserList() {
        String resultSortType = service.getUserListSortType(3);
        assertEquals("name", resultSortType);
    }

    //Test that setting the user list sort type works
    @Test
    void givenValidSortType_getUserList() {
        String testSortType = "test sort type";
        service.setUserListSortType(3, testSortType);
        String resultSortType = service.getUserListSortType(3);
        assertEquals(testSortType, resultSortType);
    }

    //Test that getting the default user list sort type works (should be ascending)
    @Test
    void givenSortAscending_getUserList() {
        boolean resultSortType = service.isUserListSortAscending(3);
        assertTrue(resultSortType);
    }

    //Test that setting the user list sort type works
    @Test
    void givenSortAscending_setUserListSort() {
        service.setUserListSortAscending(3, false);
        boolean resultSortType = service.isUserListSortAscending(3);
        assertFalse(resultSortType);
    }

}
