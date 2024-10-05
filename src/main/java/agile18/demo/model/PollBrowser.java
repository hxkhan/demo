package agile18.demo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Records.*;


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

    public List<Poll> getAllPolls() {
        return db.getAllPolls();
    }
    // --
    public List<Poll> getMunPolls(MuniRegion muniregi) {
        return getMunPolls(muniregi, null);
    }
    public List<Poll> getMunPolls(MuniRegion muniregi, PollStatusEnum pollStatusEnum) {


        return db.getMuniPolls(muniregi.municipality(), pollStatusEnum);
    }
    public List<Poll> getRegPolls(MuniRegion muniregi) {
        return getRegPolls(muniregi, PollStatusEnum.NotPassed);
    }
    public List<Poll> getRegPolls(MuniRegion muniregi, PollStatusEnum pollStatusEnum) {
        List<Poll> allPolls = db.getAllPolls();
        List<Poll> regPolls = new ArrayList<>();
        for (Poll p : allPolls) {
            if (p.home().region().equals(muniregi.region()) && isPollDateInRange(p, pollStatusEnum)) {
                regPolls.add(p);
            }
        }
        return regPolls;
    }
    // Method for filtering polls for the national level 
    public List<Poll> getNatPolls() {
        return getNatPolls(null);
    }
    public List<Poll> getNatPolls(PollStatusEnum pe){
        List<Poll> allPolls = db.getAllPolls(); 
        List<Poll> natPolls = new ArrayList<>();
        for (Poll poll : allPolls){
            if (poll.level() == LevelEnum.National && isPollDateInRange(poll, pe)){
                natPolls.add(poll);
            }
        }
        return natPolls;
    }

    // Method for filtering polls created by a specific citizen
    public List<Poll> getPollsByCitizen(Citizen c){
        return getPollsByCitizen(c, null);
    }
    
    public List<Poll> getPollsByCitizen(Citizen c, PollStatusEnum pe) {
        List<Poll> allPolls = db.getAllPolls(); 
        List<Poll> citizenPolls = new ArrayList<>();

        for (Poll poll : allPolls) {
            if (poll.creator().equals(c.id()) && isPollDateInRange(poll, pe)) { 
                citizenPolls.add(poll);
            }
        }
        return citizenPolls;
    }


    /**
     * Return a list of polls that a given citizen is eligible to vote for.
     * @param c a given Citizen.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c) {
        return getEligiblePolls(c, null, null);
    }

    /**
     * Return a list of polls that a given citizen is eligible to vote for,
     * with possible filters for a polls status.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c, PollStatusEnum s) {
        return getEligiblePolls(c, s, null);
    }

    /**
     * Return a list of polls that a given citizen is eligible to vote for,
     * with possible filters for a polls level.
     * @param c a given Citizen.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c, LevelEnum l) {
        return getEligiblePolls(c, null, l);
    }

    /**
     * Return a list of polls that a given citizen is eligible to vote for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getEligiblePolls(Citizen c, PollStatusEnum s, LevelEnum l){
        List<Poll> polls = db.getAllPolls();

        int i = 0;

        while (i < polls.size()){
            Poll p = polls.get(i);
            boolean valid = isPollDateInRange(p, s) && isPollInLevel(p, l);

            if (!isCitizenEligible(c, p) || !valid)
                polls.remove(i);
            else
                ++i;
        }
        return polls;
    }

    /**
     * Returns a list of all polls that a given citizen has voted for.
     * @param c a given Citizen.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c){
        return getVotedPolls(c, null, null);
    }

    /**
     * Returns a list of all polls that a given citizen has voted for,
     * with possible filters for a polls status.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c, PollStatusEnum s){
        return getVotedPolls(c, s, null);
    }

    /**
     * Returns a list of all polls that a given citizen has voted for,
     * with possible filters for a polls level.
     * @param c a given Citizen.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c, LevelEnum l) {
        return getVotedPolls(c, null, l);
    }

    /**
     * Returns a list of all polls that a given citizen has voted for,
     * with possible filters for a polls status and level.
     * @param c a given Citizen.
     * @param s any polls without this status is filtered out, null value filters nothing.
     * @param l any polls not on this level is filtered out, null value filters nothing.
     * @return a list of polls.
     */
    public List<Poll> getVotedPolls(Citizen c, PollStatusEnum s, LevelEnum l){
        List<Poll> polls = db.getAllPolls();

        int i = 0;

        while (i < polls.size()){
            Poll p = polls.get(i);
            boolean valid = isPollDateInRange(p, s) && isPollInLevel(p, l);

            if (!db.hasCast(c, p.id()) || !valid)
                polls.remove(i);
            else
                ++i;
        }
        return polls;
    }


    // ----- HELPERS -----
    private boolean isCitizenEligible(Citizen c, Poll p) {
        if (db.hasCast(c, p.id()))
            return false;

        switch(p.level()){
            case National:
                return true;
            case Regional:
                if (c.home().region().equals(p.home().region()))
                    return true;
            case Municipal:
                if (c.home().municipality().equals(p.home().municipality()))
                    return true;
        }
        return false;
    }
    private boolean isPollInLevel(Poll p, LevelEnum l) {
        return l == null || p.level() == l;
    }

    private boolean isPollDateInRange(Poll p, PollStatusEnum s) {
        PollStatusEnum _s;

        try {
            _s = getPollStatus(p);
        } catch (ParseException e) {
            return false;
        }

        if (s == null)
            return true;
        if (s == PollStatusEnum.Passed && _s == PollStatusEnum.Passed)
            return true;
        if (s == PollStatusEnum.Future && _s == PollStatusEnum.Future)
            return true;
        if (s == PollStatusEnum.Active && _s == PollStatusEnum.Active)
            return true;
        if (s == PollStatusEnum.NotPassed && (_s == PollStatusEnum.Active || _s == PollStatusEnum.Future))
            return true;

        return false;
    }

    private PollStatusEnum getPollStatus(Poll p) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        String start = p.startDate();
        String end = p.endDate();

        if(sdf.parse(end).before(today))
            return PollStatusEnum.Passed;

        if (sdf.parse(start).after(today))
            return PollStatusEnum.Future;

        return PollStatusEnum.Active;
    }
    
}
