package agile18.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import agile18.demo.model.Database.Database;


/*  ------------------ Before starting -----------------
    Make sure to change the spring.datasource.url to the following in the application properties.

    spring.datasource.url=jdbc:h2:file:./mock;AUTO_SERVER=TRUE
*/
@SpringBootTest
public class PollingStationTests {
    @Autowired
    private Database db;
    @Autowired
    private PollingStation ps;
    @Autowired
    private PollBrowser pb;
    
    @Test
    public void testAddTopicToPoll(){
        String e1 = "";
        String e2 = "";

        try {
            ps.addTopicToPoll(0, "Economy");
        } catch (Exception e) {
            e1 = e.getMessage();
        }
        try {
            ps.addTopicToPoll(1,"Education");
        } catch (Exception e) {
            e2 = e.getMessage();
        }

        assertEquals("Poll already have this topic!", e1);
        assertEquals("", e2);

        // test if you can see the topics in the table aswell using pollBrowser when finished
    }

    @Test
    public void testRemoveTopicFromPoll(){
        String e1 = "";
        String e2 = "";
       
        try {
            ps.removeTopicFromPoll(1, "Economy");
        } catch (Exception e) {
            e1 = e.getMessage();
        }
        try {
            ps.removeTopicFromPoll(1,"placeholder"); // Make this Economy when Database.java is finshed
        } catch (Exception e) {
            e2 = e.getMessage();
        }

        assertEquals("", e1);
        assertEquals("Poll does not have this topic!", e2);

        // check if the topic is removed in the table when pollBrowser is finished.
    }

    @BeforeEach
    public void setUp() {
        List<String> sql = Arrays.asList(
            "INSERT INTO Region VALUES ('Västra Götaland');",
            "INSERT INTO Municipality VALUES ('Trollhättan', 'Västra Götaland');",
            "INSERT INTO Citizen VALUES ('0305251111', 'Hassan', 'Khan','hejsan123', 'Trollhättan');",
            "INSERT INTO Poll VALUES (0,'0305251111', 'Trollhättan', 'Regional', 'Prohibit Alcohol on campus!', 'Self explanatory!', '2024-10-01', '2024-10-31', 0, 0, 0);",
            "INSERT INTO Poll VALUES (1,'0305251111', 'Trollhättan', 'National', 'Invade Denmark!', 'Take back what is ours!', '2024-10-01', '2024-10-31', 0, 0, 0);",
            "INSERT INTO PollTopic VALUES (0,'Economy')",
            "INSERT INTO PollTopic VALUES (1,'Economy')",
            "INSERT INTO PollTopic VALUES (0,'Education')"
        );
        for (String s : sql) {
            db.executeHelper(s);
        }
    }
    @AfterEach
    public void tearDown() {
        List<String> sql = Arrays.asList(
            "DELETE FROM PollTopic;",
            "DELETE FROM Poll;",
            "DELETE FROM Citizen;",
            "DELETE FROM Municipality;",
            "DELETE FROM Region;"
        );
        for (String s : sql) {
            db.executeHelper(s);
        }
    }
    


}
