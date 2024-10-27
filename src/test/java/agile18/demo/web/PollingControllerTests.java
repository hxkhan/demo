package agile18.demo.web;

import agile18.demo.model.*;
import agile18.demo.model.Database.Database;
import agile18.demo.model.Records.Poll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@TestPropertySource("classpath:testapplication.properties")
@SpringBootTest
@AutoConfigureMockMvc
public class PollingControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Database db;
    @Autowired
    private PollingStation ps;

    private final SqlSetUp su = new SqlSetUp();
    private String uuid;

    @Test
    void onCreatePollSuccess() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true)
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assertEquals("test", poll.getFirst().title());
    }
    @Test
    void onCreatePollInvalidUUID() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=invalid")
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid uuid")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollNotLoggedIn() throws Exception {
        String fake1 = "b25e7996-b9a5-4cf4-9c86-65e0b2363750";
        String fake2 = "852ad138-41bb-4519-919e-8de4a9e257d5";
        if (uuid.equals(fake1))
            uuid = fake2;
        else
            uuid = fake1;

        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("not logged in")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollInvalidDates1() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2025-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollInvalidDates2() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"abc\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollInvalidDates3() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-10-15\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollNullLevel() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": null,\n" +
                                "  \"startDate\": \"2025-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollEmptyTitle() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollEmptyBody() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollEmptyStart() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"\",\n" +
                                "  \"endDate\": \"2024-12-01\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCreatePollEmptyEnd() throws Exception {
        mockMvc
                .perform(post("/create-poll?uuid=" + uuid)
                        .content("{\n" +
                                "  \"title\": \"test\",\n" +
                                "  \"body\": \"testbody\",\n" +
                                "  \"level\": \"Municipal\",\n" +
                                "  \"startDate\": \"2024-11-01\",\n" +
                                "  \"endDate\": \"\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getPollsByCreator(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Future, LevelFilterEnum.Municipal);
        assert(poll.isEmpty());
    }
    @Test
    void onCastVoteSuccess() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 3,\n" +
                                "  \"vote\": \"Favor\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true)
                );
        List<Poll> poll = db.getVotedPolls(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.National);
        assertEquals("Invade Denmark!", poll.getFirst().title());
        assertEquals(1, poll.getFirst().favor());
    }
    @Test
    void onCastVoteUnauthorised() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 4,\n" + //wrong region
                                "  \"vote\": \"Favor\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("unauthorised to vote")
                );
        List<Poll> poll = db.getRegPolls("Halland", PollStatusEnum.All);
        assertEquals("Redirect pension fund!", poll.getFirst().title());
        assertEquals(0, poll.getFirst().favor());
    }
    @Test
    void onCastVoteNoPoll() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 25,\n" +
                                "  \"vote\": \"Favor\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("poll does not exist")
                );
        Poll poll = db.getPollWithID(25);
        assertNull(poll);
    }
    @Test
    void onCastVoteNotLoggedIn() throws Exception{
        String fake1 = "b25e7996-b9a5-4cf4-9c86-65e0b2363750";
        String fake2 = "852ad138-41bb-4519-919e-8de4a9e257d5";
        if (uuid.equals(fake1))
            uuid = fake2;
        else
            uuid = fake1;
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 3,\n" +
                                "  \"vote\": \"Favor\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("not logged in")
                );
        List<Poll> poll = db.getVotedPolls(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.National);
        assert(poll.isEmpty());
    }
    @Test
    void onCastVoteInvalidUUID() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=invalid")
                        .content("{\n" +
                                "  \"id\": 3,\n" +
                                "  \"vote\": \"Favor\"\n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid uuid")
                );
        List<Poll> poll = db.getVotedPolls(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.National);
        assert(poll.isEmpty());
    }
    @Test
    void onCastVoteNullVote() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 3,\n" +
                                "  \"vote\": null \n" +
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid request body")
                );
        List<Poll> poll = db.getVotedPolls(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.National);
        assert(poll.isEmpty());
    }
    @Test
    void onCastVoteBadRequest() throws Exception{
        mockMvc
                .perform(post("/cast-vote?uuid=" + uuid)
                        .content("{\n" +
                                "  \"id\": 3,\n" + // no vote
                                "}")
                        .contentType(APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isBadRequest()
                );
        List<Poll> poll = db.getVotedPolls(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.National);
        assert(poll.isEmpty());
    }
    @Test
    void onGetNationalPollSuccess() throws Exception {
        List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
        mockMvc
                .perform(get("/national-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true),
                        jsonPath("casted").value(casted)
                );
    }
    @Test
    void onGetNationalPollInvalidUUID() throws Exception {
        mockMvc
                .perform(get("/national-polls?uuid=invalid&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid uuid")
                );
    }
    @Test
    void onGetNationalPollNotLoggedIn() throws Exception {
        String fake1 = "b25e7996-b9a5-4cf4-9c86-65e0b2363750";
        String fake2 = "852ad138-41bb-4519-919e-8de4a9e257d5";
        if (uuid.equals(fake1))
            uuid = fake2;
        else
            uuid = fake1;
        mockMvc
                .perform(get("/national-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("not logged in")
                );
    }
    @Test
    void onGetNationalPollBadRequest() throws Exception {
        mockMvc
                .perform(get("/national-polls?uuid=" + uuid + "&status=poo")
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }
    @Test
    void onGetRegionalPollSuccess() throws Exception {
        List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true),
                        jsonPath("casted").value(casted)
                );
    }
    @Test
    void onGetRegionalInvalidUUID() throws Exception {
        mockMvc
                .perform(get("/regional-polls?uuid=invalid&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid uuid")
                );
    }
    @Test
    void onGetRegionalPollNotLoggedIn() throws Exception {
        String fake1 = "b25e7996-b9a5-4cf4-9c86-65e0b2363750";
        String fake2 = "852ad138-41bb-4519-919e-8de4a9e257d5";
        if (uuid.equals(fake1))
            uuid = fake2;
        else
            uuid = fake1;
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("not logged in")
                );
    }
    @Test
    void onGetRegionalPollBadRequest() throws Exception {
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=poo")
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }
    @Test
    void onGetMunicipalPollSuccess() throws Exception {
        List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(true),
                        jsonPath("casted").value(casted)
                );
    }
    @Test
    void onGetMunicipalPollInvalidUUID() throws Exception {
        mockMvc
                .perform(get("/regional-polls?uuid=invalid&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("invalid uuid")
                );
    }
    @Test
    void onGetMunicipalPollNotLoggedIn() throws Exception {
        String fake1 = "b25e7996-b9a5-4cf4-9c86-65e0b2363750";
        String fake2 = "852ad138-41bb-4519-919e-8de4a9e257d5";
        if (uuid.equals(fake1))
            uuid = fake2;
        else
            uuid = fake1;
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=Active")
                )
                .andExpectAll(
                        status().isOk(), content().contentType(APPLICATION_JSON),
                        jsonPath("success").value(false),
                        jsonPath("message").value("not logged in")
                );
    }
    @Test
    void onGetMunicipalPollBadRequest() throws Exception {
        mockMvc
                .perform(get("/regional-polls?uuid=" + uuid + "&status=poo")
                )
                .andExpectAll(
                        status().isBadRequest()
                );
    }
    @BeforeEach
    void setUp(){
        su.setUpDB(db, "testinserts.sql");
        try {
            uuid = mockMvc.perform(post("/login")
                    .contentType(APPLICATION_JSON)
                    .content("{\n\"id\": \"0305251111\", \n\"password\": \"hejsan123\"}"))
                    .andReturn().getResponse().getContentAsString();
            uuid = uuid.substring(uuid.indexOf("uuid\":\"")+7);
            uuid = uuid.substring(0, uuid.indexOf("\""));


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @AfterEach
    void tearDown(){
        su.clearDB(db);
    }


}
