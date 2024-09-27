package agile18.demo.model;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Poll;

@Service
public class PollingStation {
    private final Database db;
    private final Onboarder ob;

    public PollingStation(Database db, Onboarder ob) {
        this.db = db;
        this.ob = ob;
    }

    public List<Poll> getAllPolls() {
        return db.getAllPolls();
    }

    public void createPoll(UUID accessToken, String title, String body, String level, String startDate, String endDate) throws NotLoggedInException {
        System.out.println("Create Poll called!");
        Citizen creator = ob.checkLogin(accessToken);
        Level lev = Level.valueOf(level);
        db.createPoll(creator, lev, title, body, startDate, endDate);
    }
}
