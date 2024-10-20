package agile18.demo.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private final SqlSetUp su = new SqlSetUp();
    @Test
    public void testAddTopicToPoll(){
        String e1 = "";
        String e2 = "";

        try {
            ps.addTopicToPoll(1, "Climate");
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
            ps.removeTopicFromPoll(1, "Climate");
        } catch (Exception e) {
            e1 = e.getMessage();
        }
        try {
            ps.removeTopicFromPoll(1,"Education"); // Make this Economy when Database.java is finshed
        } catch (Exception e) {
            e2 = e.getMessage();
        }

        assertEquals("", e1);
        assertEquals("Poll does not have this topic!", e2);

        // check if the topic is removed in the table when pollBrowser is finished.
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
