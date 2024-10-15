package agile18.demo.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import agile18.demo.model.NewsStation;
import agile18.demo.model.Onboarder;
import agile18.demo.model.SqlSetUp;
import agile18.demo.model.Database.Database;
import agile18.demo.model.Records.NewsPost;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;


@TestPropertySource("classpath:testapplication.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class NewsControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Database db;
    @Autowired
    private Onboarder ob;
    @Autowired
    private NewsStation ns;
  
    private final SqlSetUp su = new SqlSetUp();

    @Test
    void onFavorNewsNotLoggedIn() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .param("uuid",UUID.randomUUID().toString())
                .content("{\n \"newsId\": \"1\", \n\"favorable\": \"true\"\n}")
                .contentType(APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("Not logged in")
            );
    }
    @Test
    void onFavorNewsOk() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .param("uuid",uuid.toString())
                .content("{\n \"newsId\": \"1\", \n\"favorable\": \"true\"\n}")
                .contentType(APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(true)
            );
    }
    @Test //This shouldnt work??
    void onFavorNewsWrongContent() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .param("uuid",uuid.toString())// content wrong??
                .content("{\n \"id\": \"0311261111\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON)
            )
            .andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false)
            );
    }

    @Test // not sure how to test badRequest
    void onFavorNewsBadRequest() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/favor-news")
                .content("{\n \"newsId\": \"1\", \n\"favorable\": \"true\"\n}")
                .contentType(APPLICATION_JSON))
            .andExpectAll(status().isBadRequest(), 
                result -> assertEquals(
                    "Required parameter 'uuid' is not present.", 
                    result.getResponse().getErrorMessage()));
    }
    @Test
    void onPostNewsPass() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", uuid.toString())
                .content("{\n \"title\": \"test post\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(true));
    }
    @Test
    void onPostNewsInvalidReqBody1() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", uuid.toString())
                .content("{\n \"title\": \"\", \n\"body\": \"test body\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid request body"));
    }
    @Test
    void onPostNewsInvalidReqBody2() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", uuid.toString())
                .content("{\n \"title\": \"test\", \n\"body\": \"\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid request body"));
    }
    @Test
    void onPostNewsInvalidReqBody3() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", uuid.toString())
                .content("{\n \"title\": \"test\", \n\"bod\": \"test\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid request body"));
    }
    @Test
    void onPostNewsInvalidReqBody4() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", uuid.toString())
                .content("{\n \"titl\": \"test\", \n\"body\": \"test\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid request body"));
    }
    @Test
    void onPostNewsInvalidUUID() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", "123")
                .content("{\n \"title\": \"test\", \n\"body\": \"test\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid uuid"));
    }
    @Test
    void onPostNewsNotLoggedIn() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/post-news")
                .param("uuid", UUID.randomUUID().toString())
                .content("{\n \"title\": \"test\", \n\"body\": \"test\"\n}")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("not logged in"));
    }

    @Test
    void onGetNewsPass() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(get("http://localhost:8080/news")
                .param("uuid", uuid.toString())
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(true),
                jsonPath("isSecretary").value(false));
    }
    @Test
    void onGetNewsIsSecretary() throws Exception{
        UUID uuid = ob.login("0305251111", "hejsan123");
        mockMvc
            .perform(get("http://localhost:8080/news")
                .param("uuid", uuid.toString())
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(true),
                jsonPath("isSecretary").value(true));
    }
    @Test
    void onGetNewsInvalidUUID() throws Exception{
        mockMvc
            .perform(get("http://localhost:8080/news")
                .param("uuid", "hej")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("invalid uuid"));
    }
    @Test
    void onGetNewsNotLoggedIn() throws Exception{
        mockMvc
            .perform(get("http://localhost:8080/news")
                .param("uuid", UUID.randomUUID().toString())
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), content().contentType(APPLICATION_JSON),
                jsonPath("success").value(false),
                jsonPath("message").value("not logged in"));
    }
    @Test // We dont have check for this
    void onGetNewsInvalidBody() throws Exception{
        assertTrue(false);
    }
    @Test
    void onGetSingleNewsPass() throws Exception{
        NewsPost ns1 = ns.getSingleNews(1); 
        mockMvc
            .perform(get("http://localhost:8080/single-news")
                .param("news", "1")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), result -> assertEquals(
                    helperNewsPostToString(ns1), 
                    result.getResponse().getContentAsString())
                );
    } 
    @Test // Does not work, group by id?
    void onGetSingleNewsFail() throws Exception{
        NewsPost ns1 = ns.getSingleNews(2); 
        mockMvc
            .perform(get("http://localhost:8080/single-news")
                .param("news", "2")
                .contentType(APPLICATION_JSON)
            ).andExpectAll(
                status().isOk(), result -> assertEquals(
                    helperNewsPostToString(ns1), 
                    result.getResponse().getContentAsString())
                );
    }
    @Test
    void onGetNewsCommentsPass() throws Exception{
        mockMvc
            .perform(get("http://localhost:8080/news-comments")
            .param("news", "1")
            .contentType(APPLICATION_JSON)
        ).andExpectAll(
            status().isOk(), result -> assertEquals(
                "[{\"user\":\"Sebastian Kolbel\",\"comment\":\"good news\",\"date\":\"2024-10-07 20:34:12\"}]",
                result.getResponse().getContentAsString())
            );
    }
    @Test
    void onGetNewsCommentsFail() throws Exception{
        mockMvc
            .perform(get("http://localhost:8080/news-comments")
            .param("new", "2")
            .contentType(APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest(), result -> assertEquals(
                "Required parameter 'news' is not present.",
                result.getResponse().getErrorMessage())
            );
    }
    @Test
    void onPostCommentPass() throws Exception{
        UUID uuid = ob.login("9603291111", "hejhallo");
        mockMvc
            .perform(post("http://localhost:8080/post-comment")
            .param("uuid", uuid.toString())
            .content("{\n \"newsId\": \"1\", \n\"comment\": \"nice comment\"\n}")
            .contentType(APPLICATION_JSON)
        ).andExpectAll(
            status().isOk(), content().contentType(APPLICATION_JSON),
            jsonPath("success").value(true));
    }
    @Test
    void onPostCommentNotLoggedIn() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/post-comment")
            .param("uuid", UUID.randomUUID().toString())
            .content("{\n \"newsId\": \"1\", \n\"comment\": \"nice comment\"\n}")
            .contentType(APPLICATION_JSON)
        ).andExpectAll(
            status().isOk(), content().contentType(APPLICATION_JSON),
            jsonPath("success").value(false),
            jsonPath("message").value("not logged in"));
    }
    @Test
    void onPostCommentInvalidBadUUID() throws Exception{
        mockMvc
            .perform(post("http://localhost:8080/post-comment")
            .param("uui", UUID.randomUUID().toString())
            .content("{\n \"newsId\": \"1\", \n\"comment\": \"nice comment\"\n}")
            .contentType(APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest(), result -> assertEquals(
                "Required parameter 'uuid' is not present.",
                result.getResponse().getErrorMessage())
            );
    }

    String helperNewsPostToString(NewsPost np) {
        return "{\"id\":"+np.id()+"," +
                "\"title\":\""+np.title()+"\"," +
                "\"body\":\""+np.body()+"\"," +
                "\"date\":\""+np.date()+"\"," +
                "\"favorable\":"+np.favorable()+"," +
                "\"unfavorable\":"+np.unfavorable()+"}";
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
