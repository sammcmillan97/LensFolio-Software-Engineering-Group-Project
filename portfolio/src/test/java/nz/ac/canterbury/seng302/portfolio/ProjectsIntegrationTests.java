package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProjectsIntegrationTests {
    @Autowired
    private ProjectEntityRepository projectEntityRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanDatabase() {
        projectEntityRepository.deleteAll();
    }

    @Test
    void validateDeleteRequest() throws Exception {
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        ProjectEntity project2 = new ProjectEntity("Project2", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        projectEntityRepository.save(project1);
        projectEntityRepository.save(project2);
        String id = project1.getProject_id().toString();
        mockMvc.perform(delete("/projects").param("id", id)).andExpect(status().is3xxRedirection());
        ProjectEntity project = projectEntityRepository.findById(project2.getProject_id()).orElse(null);
        assertThat(project).isNotNull();
        ProjectEntity project3 = projectEntityRepository.findById(project1.getProject_id()).orElse(null);
        assertThat(project3).isNull();
    }

    @Test
    void validateDefaultProject() throws Exception {
        Iterable<ProjectEntity> projects = projectEntityRepository.findAll();
        int counter = 0;
        for (Object i : projects) {
            counter++;
        }
        assertThat(counter).isEqualTo(0);
        mockMvc.perform(get("/projects")).andExpect(status().isOk());
        Iterable<ProjectEntity> projects2 = projectEntityRepository.findAll();
        int counter2 = 0;
        for (Object i : projects2) {
            counter2++;
        }
        assertThat(counter2).isEqualTo(1);
    }

}