package agile18.demo.model;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.*;
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

    public void createPoll(UUID accessToken, String title, String body, LevelEnum level, String startDate, String endDate) throws NotLoggedInException {
        Citizen creator = ob.checkLogin(accessToken);
        db.createPoll(creator, level, title, body, startDate, endDate);
    }

    public Poll getPollWithID(int id) throws PollDoesNotExistException {
        return db.getPollWithID(id);
    }

    public List<Poll> getAllPolls() {
        return db.getAllPolls();
    }

    public void castVote(UUID accessToken, int pollID, VoteEnum vote) throws NotLoggedInException, PollDoesNotExistException, CitizenHasAlreadyCastedException {
        Citizen voter = ob.checkLogin(accessToken);
        Poll poll = db.getPollWithID(pollID);

        if (!db.canCast(voter, pollID)) throw new CitizenHasAlreadyCastedException();

        switch (poll.level()) {
            case LevelEnum.National:
                db.castVote(voter, pollID, vote); 
                break;

            /* case LevelEnum.Regional:

                db.castVote(voter, pollID, vote); 
                break;

            case LevelEnum.Municipal:
                db.castVote(voter, pollID, vote); 
                break; */
        }
    }
}
