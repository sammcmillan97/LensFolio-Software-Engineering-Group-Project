package nz.ac.canterbury.seng302.portfolio.model;

import nz.ac.canterbury.seng302.portfolio.repository.PortfolioUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase
@SpringBootTest
class PortfolioUserTests {

    @Autowired
    PortfolioUserRepository repository;


    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    int userId = 1;

    @Test
    void addSkillsToUser_retrieveFromLocal(){
        PortfolioUser portfolioUser = new PortfolioUser(userId, "name", true);
        portfolioUser.addSkill("skill");
        Collection<String> skills = new ArrayList<>();
        skills.add("skill");
        assertEquals(skills, portfolioUser.getSkills());
    }

    @Test
    @Transactional
    void addSkillsToUser_retrieveFromDatabase(){
        PortfolioUser portfolioUser = new PortfolioUser(userId, "name", true);
        portfolioUser.addSkill("skill");
        repository.save(portfolioUser);

        Collection<String> skills = new ArrayList<>();
        skills.add("skill");

        PortfolioUser portfolioUser1 = repository.findByUserId(userId);
        assertEquals(skills, portfolioUser1.getSkills());
    }
}
