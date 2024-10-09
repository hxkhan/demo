package agile18.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Records.*;


@SpringBootTest
public class PollBrowserTests {
    int pollId;
    @Autowired
    private Database db;
    @Autowired
    private PollBrowser pb;
    // WARNING: You must change the path inside SqlSetUp class or else tests wont work
    private final SqlSetUp su = new SqlSetUp();

    @Test
	void testGetMunPolls() {

        MuniRegion göteVgöta = new MuniRegion("Trollhättan", "Västra Götaland");
        List<Poll> test = pb.getMunPolls(göteVgöta,PollStatusEnum.All);
        Poll poll = new Poll(0, "0305251111",new MuniRegion("Trollhättan","Västra Götaland"),
                LevelEnum.Municipal, "Tear down MÅG", "Valid argument tbh!",
                "2024-09-01", "2024-09-07",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(expected,test);
	}
    @Test
    void testGetRegPolls() {

        MuniRegion varbergVHalland = new MuniRegion("Varberg", "Halland");
        List<Poll> test = pb.getRegPolls(varbergVHalland, PollStatusEnum.All);
        Poll poll = new Poll(4, "9603291111",varbergVHalland,
                LevelEnum.Regional, "Redirect pension fund!", "I think we should redirect our VGR pension fund to stock options on the oil market!",
                "2025-10-21", "2025-10-25",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(expected,test);

	}
    @Test
    void testGetNatPolls() {

        MuniRegion varbergVHalland = new MuniRegion("Härryda", "Västra Götaland");
        List<Poll> test = pb.getNatPolls(PollStatusEnum.Future);
        Poll poll = new Poll(5, "0311261111",varbergVHalland,
                LevelEnum.National, "Leave EU!", "Keep Sweden Swedish, we make our own decisions about our future! USB C sucks!",
                "2025-11-01", "2025-11-30",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(expected,test);
	}

    @Test
    void testGetPollsByCitizen1(){

        List<Poll> polls = pb.getPollsByCitizen(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.All, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(),1);
        assertEquals(polls.get(1).id(),2);
    }

    @Test
    void testGetPollsByCitizen2(){
        List<Poll> polls = pb.getPollsByCitizen(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Active, LevelFilterEnum.All);
        assertEquals(3, polls.get(0).id());
        assertEquals(1, polls.size());

        polls = pb.getPollsByCitizen(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Past, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(),0);
        assertEquals(polls.size(),1);
    }

    @Test
    void testGetVotedPolls1() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.All, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.get(1).id(), 2);
        assertEquals(polls.get(2).id(), 5);
        assertEquals(polls.size(),3);
    }

    @Test
    void testGetVotedPolls2() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.Active, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 2);
        assertEquals(polls.size(),1);
    }

    @Test
    void testGetVotedPolls3() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"),PollStatusEnum.All, LevelFilterEnum.Regional);
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.get(1).id(), 2);
        assertEquals(polls.size(), 2);
    }

    @Test
    void testGetVotedPolls4() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.Past, LevelFilterEnum.Regional);
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.size(),1);
    }

    @Test
    void testGetEligiblePolls1(){
        List<Poll> polls = pb.getEligiblePolls(db.getCitizenWithPersonNumber("9603291111"), PollStatusEnum.All, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 3);
        assertEquals(polls.get(1).id(), 4);
        assertEquals(polls.get(2).id(), 5);
        assertEquals(polls.size(),3);
    }

    @Test
    void testGetEligiblePolls2(){
        List<Poll> polls = pb.getEligiblePolls(db.getCitizenWithPersonNumber("9603291111"), PollStatusEnum.Active, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 3);
        assertEquals(polls.size(),1);
    }

    @Test
    void testGetEligiblePolls3(){
        List<Poll> polls = pb.getEligiblePolls(db.getCitizenWithPersonNumber("9603291111"), PollStatusEnum.All, LevelFilterEnum.National);
        assertEquals(polls.get(0).id(), 3);
        assertEquals(polls.get(1).id(), 5);
        assertEquals(polls.size(),2);
    }

    @Test
    void testGetPollTopics(){
        List<String> topics = pb.getPollTopics(2);
        assertEquals(topics.get(0), "cool");
        assertEquals(topics.get(1), "lame");
        assertEquals(topics.size(),2);
    }
    @Test
    void testGetPollWithTopics(){
        List<Poll> polls = pb.getPollsWithTopic("lame");
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.get(1).id(), 2);
        assertEquals(polls.size(),2);
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
