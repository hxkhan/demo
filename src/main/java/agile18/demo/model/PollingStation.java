package agile18.demo.model;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.CitizenHasAlreadyCastedException;
import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Exceptions.PollAlreadyHasTopicException;
import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Exceptions.PollDoesNotHaveTopicException;
import agile18.demo.model.Exceptions.UnAuthorisedToVote;
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
        return db.getAllPolls(PollStatusEnum.All);
    }
    public void addTopicToPoll(int pollID, String topic) throws PollAlreadyHasTopicException{
        if (db.pollTopicExists(pollID,topic)) throw new PollAlreadyHasTopicException();
        db.addTopicToPoll(pollID, topic);
    }

    public void removeTopicFromPoll(int pollID, String topic) throws PollDoesNotHaveTopicException{
        if (!db.pollTopicExists(pollID, topic)) throw new PollDoesNotHaveTopicException();
        db.removeTopicFromPoll(pollID, topic);
   }

    public void castVote(UUID accessToken, int pollID, VoteEnum vote) 
    throws NotLoggedInException, PollDoesNotExistException, CitizenHasAlreadyCastedException, UnAuthorisedToVote {
        Citizen voter = ob.checkLogin(accessToken);
        Poll poll = db.getPollWithID(pollID);
        if (poll == null) throw new PollDoesNotExistException();

        // if the voter has already cast in this poll
        if (db.hasCast(voter, poll.id())) throw new CitizenHasAlreadyCastedException();

        switch (poll.level()) {
            case LevelEnum.National:
                db.castVote(voter, poll.id(), vote); 
                break;

            case LevelEnum.Regional:
                // if poll.region != voter.region
                if (!poll.home().region().equals(voter.home().region())) throw new UnAuthorisedToVote();
                db.castVote(voter, poll.id(), vote);
                break;

            case LevelEnum.Municipal:
                // if poll.municipality != voter.municipality
                if (!poll.home().municipality().equals(voter.home().municipality())) throw new UnAuthorisedToVote();
                db.castVote(voter, poll.id(), vote); 
                break;
        }
    }
}
