package agile18.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Records.MuniRegion;
import agile18.demo.model.Records.Poll;

@SpringBootTest
public class PollBrowserTests {
    int pollId;
    @Autowired
    private Database db;
    @Autowired
    private PollBrowser pb;

    @Test
	void testGetMunPolls() {
        MuniRegion göteVgöta = new MuniRegion("Borås", "Västra Götaland");
        List<Poll> test = pb.getMunPolls(göteVgöta,PollStatusEnum.All);
        Poll poll = new Poll(0, "0305251111",new MuniRegion("Borås","Västra Götaland"), 
                LevelEnum.Municipal, "Tear down Borås!", "Valid argument tbh!", 
                "2024-09-28", "2024-09-30",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(expected,test);
	}
    @Test
    void testGetRegPolls() {
        MuniRegion göteVgöta = new MuniRegion("Göteborg", "Västra Götaland");
        List<Poll> test = pb.getRegPolls(göteVgöta, PollStatusEnum.All);
        Poll poll = new Poll(1, "0311261111",göteVgöta, 
                LevelEnum.Regional, "Dance prohibition!", "Please no dancing!", 
                "2024-10-01", "2024-10-05",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(expected,test);
	}
    @Test
    void testGetNatPolls() {
        MuniRegion varbergVHalland = new MuniRegion("Varberg", "Halland");
        List<Poll> test = pb.getNatPolls(PollStatusEnum.All);
        Poll poll = new Poll(2, "0311261111",varbergVHalland, 
                LevelEnum.National, "Redirect pension fund!", "I think we should redirect our pension fund to stock options on the oil market!", 
                "2024-10-03", "2024-10-10",0, 0, 0);
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
        assertEquals(polls.size(), 0);

        polls = pb.getPollsByCitizen(db.getCitizenWithPersonNumber("0305251111"), PollStatusEnum.Past, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(),0);
        assertEquals(polls.size(),1);
    }

    @Test
    void testGetVotedPolls1() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.All, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.get(1).id(), 2);
    }
    @Test
    void testGetVotedPolls2() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.Active, LevelFilterEnum.All);
        assertEquals(polls.get(0).id(), 2);
    }
    @Test
    void testGetVotedPolls3() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"),PollStatusEnum.All, LevelFilterEnum.Regional);
        assertEquals(polls.get(0).id(), 1);
        assertEquals(polls.size(), 1);
    }

    @Test
    void testGetVotedPolls4() {
        List<Poll> polls = pb.getVotedPolls(db.getCitizenWithPersonNumber("0311261111"), PollStatusEnum.Past, LevelFilterEnum.Regional);
        assertEquals(polls.get(0).id(), 1);
    }

    @BeforeEach
    public void setUp() {
        //MuniRegion mr = new MuniRegion("Göteborg","Västra Götaland");
        //Citizen c = new Citizen("2020032019", "test", "testson", "testo", mr);
        //pollId = db.createPoll(c,LevelEnum.Municipal,"testtitel",
        //            "testbody","2024-10-01","2024-10-10");
    }
    @AfterEach
    public void tearDown() {
        //db.removePoll(pollId);
    }
}
