package nz.ac.canterbury.seng302.portfolio.repository;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.PortfolioUserRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class PortfolioUserRepositoryTests {

    @Autowired
    private DataSource dataSource;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private EntityManager entityManager;
    @Autowired private PortfolioUserRepository portfolioUserRepository;

    @BeforeEach
    void cleanDatabase() {
        portfolioUserRepository.deleteAll();
    }

    // Test that the database has connected correctly
    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(portfolioUserRepository).isNotNull();
    }

    // Test that a full list of users can be retrieved from the database
    @Test
    void findAllUsers() {
        PortfolioUser user1 = new PortfolioUser(1, "test sort type");
        PortfolioUser user2 = new PortfolioUser(2, "test sort type 2");
        List<PortfolioUser> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        portfolioUserRepository.saveAll(users);
        List<PortfolioUser> retrievedUsers = StreamSupport.stream(portfolioUserRepository.findAll().spliterator(), false).toList();
        assertThat(retrievedUsers.get(0).getUserId()).isEqualTo(users.get(0).getUserId());
        assertThat(retrievedUsers.get(0).getUserListSortType()).isEqualTo(users.get(0).getUserListSortType());
        assertThat(retrievedUsers.get(1).getUserId()).isEqualTo(users.get(1).getUserId());
        assertThat(retrievedUsers.get(1).getUserListSortType()).isEqualTo(users.get(1).getUserListSortType());
    }

    // Test a specific user can be retrieved from the database
    @Test
    void findUserById() {
        PortfolioUser user1 = new PortfolioUser(1, "test sort type");
        PortfolioUser user2 = new PortfolioUser(2, "test sort type 2");
        List<PortfolioUser> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        portfolioUserRepository.saveAll(users);
        PortfolioUser retrievedUser = portfolioUserRepository.findByUserId(users.get(0).getUserId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUserId()).isEqualTo(users.get(0).getUserId());
        assertThat(retrievedUser.getUserListSortType()).isEqualTo(users.get(0).getUserListSortType());

    }

}
