package agile18.demo.model;

import java.util.List;

import org.springframework.stereotype.Service;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.MuniRegion;
import agile18.demo.model.Records.Poll;


@Service
public class PollBrowser {
    private final Database db;

    public PollBrowser(Database db) {
        this.db = db;
    }

    // -- maybe these should be here instead of in PollingStation?
    public Poll getPollWithID(int id) throws PollDoesNotExistException {
        return db.getPollWithID(id);
    }

    public List<Poll> getAllPolls(PollStatusEnum ps) {
        return db.getAllPolls(ps);
    }

    //--topics--
    public List<String> getPollTopics(int id){
        return db.getPollTopics(id);
    }

    public List<Poll> getPollsWithTopic(String topic){
        return db.getPollsWithTopic(topic);
    }

    public void addTopicToPoll(int id, String topic){
        db.addTopicToPoll(id, topic);
    }

    public void removeTopicFromPoll(int id, String topic){
        db.removeTopicFromPoll(id, topic);
    }
    
    // --

    public List<Poll> getMunPolls(MuniRegion mr, PollStatusEnum ps) {
        return db.getMuniPolls(mr.municipality(), ps);
    }

    public List<Poll> getRegPolls(MuniRegion mr, PollStatusEnum ps) {
        return db.getRegPolls(mr.region(), ps);
    }

    public List<Poll> getNatPolls(PollStatusEnum ps){
        return db.getNatPolls(ps);
    }

    
    public List<Poll> getPollsByCitizen(Citizen c, PollStatusEnum ps, LevelFilterEnum l) {
        //List<Poll> allPolls = db.getAllPolls(ps); 
        //List<Poll> citizenPolls = new ArrayList<>();
//
        //for (Poll poll : allPolls) {
        //    if (poll.creator().equals(c.id()) && isPollDateInRange(poll, ps)) { 
        //        citizenPolls.add(poll);
        //    }
        //}
        return db.getPollsByCreator(c,ps,l);
    }

    /**
     * Return a list of polls that a given citizen is eligible to vote for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param ps any polls without this status is filtered out.
     * @param l any polls not on this level is filtered out.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c, PollStatusEnum ps, LevelFilterEnum l){
        return db.getEligiblePolls(c, ps, l);
    }


    /**
     * Returns a list of all polls that a given citizen has voted for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param ps any polls without this status is filtered out.
     * @param l any polls not on this level is filtered out.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c, PollStatusEnum ps, LevelFilterEnum l){
        return db.getVotedPolls(c, ps, l);
    }    
}
