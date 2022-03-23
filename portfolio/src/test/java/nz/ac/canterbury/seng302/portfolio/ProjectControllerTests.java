package nz.ac.canterbury.seng302.portfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.controller.ProjectsController;
import nz.ac.canterbury.seng302.portfolio.entities.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.entities.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repositories.ProjectEntityRepository;
import nz.ac.canterbury.seng302.portfolio.repositories.SprintEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ProjectsController.class)
public class ProjectControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectEntityRepository projectEntityRepository;
    @MockBean
    private SprintEntityRepository sprintEntityRepository;

    @BeforeEach
    void initDatabase() {
        projectEntityRepository.deleteAll();
        ProjectEntity project1 = new ProjectEntity("Project1", "Test Project", Date.valueOf("2022-04-15"), Date.valueOf("2022-05-16"));
        projectEntityRepository.save(project1);

    }

    @Test
    void validateGetRequest() throws Exception {
        mockMvc.perform(get("/projects")).andExpect(status().isOk());
    }

    @Test
    void validateDeleteRequest() throws Exception {
        mockMvc.perform(delete("/projects").param("id", "1")).andExpect(status().is3xxRedirection());
    }
}
