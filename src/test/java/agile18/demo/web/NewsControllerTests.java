package agile18.demo.web;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import agile18.demo.model.SqlSetUp;
import agile18.demo.model.Database.Database;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource("classpath:testapplication.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Database db;
  
    private final SqlSetUp su = new SqlSetUp();

    @Test
    void onFavorNewsFailTest() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .content("{\n \"id\": \"1111111111\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("Not logged in")
            );

    }
    @Test
    void onFavorNewsPassTest() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .content("{\n \"id\": \"0311261111\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(true)
            );

    }

    @Test
    void onFavorNewsBRTest() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .content("{\n \"id\": \"0311261111\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON))
            .andExpectAll(status().isBadRequest());

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
