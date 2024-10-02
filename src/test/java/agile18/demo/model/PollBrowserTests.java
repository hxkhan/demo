package agile18.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import agile18.demo.model.Records.MuniRegion;
import agile18.demo.model.Records.Poll;

@SpringBootTest
public class PollBrowserTests {
    int pollId;
    @Autowired
    private Database db;
    private PollBrowser pb;

    @Test
	void testGetMunPolls() {
        MuniRegion göteVgöta = new MuniRegion("Göteborg", "Västra Götaland");
        pb = new PollBrowser(db);
        List<Poll> test = pb.getMunPolls(göteVgöta);
        Poll poll = new Poll(1, new MuniRegion("Göteborg","Västra Götaland"), 
                LevelEnum.Regional, "Dance prohibition!", "Please no dancing!", 
                "2024-10-01", "2024-10-07",0, 0, 0);
        List<Poll> expected = Arrays.asList(poll);
        assertEquals(test, expected);
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
