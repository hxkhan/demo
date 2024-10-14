package agile18.demo.web;

import agile18.demo.model.Database.*;
import agile18.demo.model.SqlSetUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OnboardingControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Database db;
    // WARNING: You must change the path inside SqlSetUp class or else tests wont work
    private final SqlSetUp su = new SqlSetUp();

    @Test
    void loginFailureTest() throws Exception{
        mockMvc.perform(post("http://localhost:8080/login")
                        .content(" {\n \"id\": \"1111111111\", \n\"password\": \"123\"\n}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("user does not exist"));
    }
    @Test
    void loginFailureTest2() throws Exception{
        mockMvc.perform(post("http://localhost:8080/login")
                        .content(" {\n \"id\": \"0305251111\", \n\"password\": \"123\"\n}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("incorrect password"));
    }

    @BeforeEach
    void setUp(){
        su.setUpDB(db, "testinserts.sql");
    }
    @AfterEach
    void tearDown(){
        su.clearDB(db);
    }

}
