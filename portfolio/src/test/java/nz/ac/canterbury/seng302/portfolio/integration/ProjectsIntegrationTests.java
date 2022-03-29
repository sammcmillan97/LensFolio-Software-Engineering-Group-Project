package nz.ac.canterbury.seng302.portfolio.integration;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Deprecated
@AutoConfigureMockMvc(addFilters = false)
class ProjectsIntegrationTests {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanDatabase() {
        projectRepository.deleteAll();
    }

    //@Test
    void validateDeleteRequest() throws Exception {
        Project project1 = new Project("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        Project project2 = new Project("Project2", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        projectRepository.save(project1);
        projectRepository.save(project2);
        String id = String.valueOf(project1.getId());
        mockMvc.perform(delete("/projects").param("id", id)).andExpect(status().is3xxRedirection());
        Project project = projectRepository.findById(project2.getId());
        assertThat(project).isNotNull();
        Project project3 = projectRepository.findById(project1.getId());
        assertThat(project3).isNull();
    }

    //@Test
    void validateDefaultProject() throws Exception {
        Iterable<Project> projects = projectRepository.findAll();
        int counter = 0;
        for (Object i : projects) {
            counter++;
        }
        assertThat(counter).isEqualTo(1);
        mockMvc.perform(get("/projects")).andExpect(status().isOk());
        Iterable<Project> projects2 = projectRepository.findAll();
        int counter2 = 0;
        for (Object i : projects2) {
            counter2++;
        }
        assertThat(counter2).isEqualTo(1);
    }

}