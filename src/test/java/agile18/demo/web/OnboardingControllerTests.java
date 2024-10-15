package agile18.demo.web;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import agile18.demo.model.SqlSetUp;
import agile18.demo.model.Database.Database;

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
    void loginFailureTest1() throws Exception{
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

    @Test
    void loginSuccessTest1() throws Exception {
        mockMvc.perform(post("http://localhost:8080/login")
                        .content("{\n\"id\": \"0305251111\", \n\"password\": \"hejsan123\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true),
                        jsonPath("uuid").isNotEmpty());
    }

    @Test
    void loginSuccessTest2() throws Exception {
        mockMvc.perform(post("http://localhost:8080/login")
                        .content("{\n\"id\": \"9603291111\", \n\"password\": \"hejhallo\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("$.success").value(true),
                        jsonPath("$.uuid").isNotEmpty());
    }

    @Test
    void registerSuccessTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/register")
                        .content("{\"firstName\": \"Johan\",\"lastName\": \"Larsson\",\"id\": \"6709227752\",\"password\": \"jonlar124\",\"municipality\":\"Trollhättan\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                    jsonPath("success").value(true),
                    jsonPath("uuid").isNotEmpty());
    }

    @Test
    void registerFailureCitizenExistsTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/register")
                        .content("{\"firstName\": \"Hassan\", \"lastName\": \"Khan\", \"id\": \"0305251111\", \"password\": \"hejsan123\", \"municipality\":\"Trollhättan\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON), 
                    jsonPath("success").value(false),
                    jsonPath("message").value("user already exists"));
    }

    @Test
    void registerMunicipalityDoesNotExistTest() throws Exception {
        mockMvc.perform(post("http://localhost:8080/register")
                        .content("{\"firstName\": \"Hassan\", \"lastName\": \"Khan\", \"id\": \"0305251111\", \"password\": \"hejsan123\", \"municipality\":\"Stockholm\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid municipality"));
    }


    @Test
    void checkLoginSuccessTest() throws Exception {
    // Login and take the UUID from the response
        String loginResponse = mockMvc.perform(post("http://localhost:8080/login")
                        .content("{\n\"id\": \"0305251111\", \n\"password\": \"hejsan123\"}")
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                            jsonPath("success").value(true),
                            jsonPath("uuid").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

    // Take the UUID from the login response 
    String uuid = extractUUIDFromResponse(loginResponse);

        // Use the UUID to check the login status
        mockMvc.perform(get("http://localhost:8080/check-login-status")
                        .param("uuid", uuid)  // Send UUID as a request parameter
                        .contentType(APPLICATION_JSON))
                .andExpectAll(status().isOk(), content().contentType(APPLICATION_JSON),
                            jsonPath("success").value(true),
                            jsonPath("data").isNotEmpty());
}

    // ----Helper method--- 
    private String extractUUIDFromResponse(String response) {
        int uuidStart = response.indexOf("\"uuid\":\"") + 8;
        int uuidEnd = response.indexOf("\"", uuidStart);
        return response.substring(uuidStart, uuidEnd);
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
