package agile18.demo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    // --

    public List<Poll> getMunPolls(MuniRegion mr, PollStatusEnum ps) {
        List<Poll> polls = db.getMuniPolls(mr.municipality(),ps);
        polls.removeIf(p -> !p.home().equals(mr) || !isPollDateInRange(p, ps));
        return polls;
    }

    public List<Poll> getRegPolls(MuniRegion mr, PollStatusEnum ps) {
        List<Poll> allPolls = db.getRegiPolls(mr.region(), ps);
        //polls.removeIf(p -> !p.home().equals(mr) || !isPollDateInRange(p, ps));
        List<Poll> regPolls = new ArrayList<>();
        for (Poll p : allPolls) {
            if (p.home().region().equals(mr.region()) && isPollDateInRange(p, ps)) {
                regPolls.add(p);
            }
        }
        return regPolls;
    }
    public List<Poll> getNatPolls(PollStatusEnum ps){
        List<Poll> allPolls = db.getAllPolls(ps); 
        List<Poll> natPolls = new ArrayList<>();
        for (Poll poll : allPolls){
            if (poll.level() == LevelEnum.National && isPollDateInRange(poll, ps)){
                natPolls.add(poll);
            }
        }
        return natPolls;
    }

    
    public List<Poll> getPollsByCitizen(Citizen c, PollStatusEnum ps) {
        List<Poll> allPolls = db.getAllPolls(ps); 
        List<Poll> citizenPolls = new ArrayList<>();

        for (Poll poll : allPolls) {
            if (poll.creator().equals(c.id()) && isPollDateInRange(poll, ps)) { 
                citizenPolls.add(poll);
            }
        }
        return citizenPolls;
    }

    /**
     * Return a list of polls that a given citizen is eligible to vote for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c, PollStatusEnum s, LevelFilterEnum l){
        List<Poll> polls = db.getEligiblePolls(c);
        polls.removeIf(p -> isPollDateInRange(p, s) && isPollInLevel(p, l));

        return polls;
    }


    /**
     * Returns a list of all polls that a given citizen has voted for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c, PollStatusEnum ps, LevelFilterEnum l){
        List<Poll> polls = db.getVotedPolls(c, ps);
        System.out.println(polls);
        polls.removeIf(p -> !isPollInLevel(p, l));
        return polls;
    }


    // ----- HELPERS -----

    private boolean isPollInLevel(Poll p, LevelFilterEnum l) {
        if (l == LevelFilterEnum.All)
            return true;
        return switch(p.level()) {
            case Municipal -> l == LevelFilterEnum.Municipal;
            case Regional -> l == LevelFilterEnum.Regional;
            case National -> l == LevelFilterEnum.National;
        };
    }

    private boolean isPollDateInRange(Poll p, PollStatusEnum s) {
        PollStatusEnum _s;
        try {
            _s = getPollStatus(p);
        } catch (ParseException e) {
            return false;
        }
        return switch(s) {
            case All -> true;
            case Past -> _s == PollStatusEnum.Past;
            case Future -> _s == PollStatusEnum.Future;
            case Active -> _s == PollStatusEnum.Active;
        };
    }

    private PollStatusEnum getPollStatus(Poll p) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        String start = p.startDate();
        String end = p.endDate();

        if(sdf.parse(end).before(today))
            return PollStatusEnum.Past;

        if (sdf.parse(start).after(today))
            return PollStatusEnum.Future;

        return PollStatusEnum.Active;
    }
    
}
