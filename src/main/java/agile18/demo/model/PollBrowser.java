package agile18.demo.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Records.*;

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
        return getMunPolls(muniregi, PollEnum.NonFinished);
    }
    public List<Poll> getMunPolls(MuniRegion muniregi, PollEnum pollEnum) {
        List<Poll> allPolls = db.getAllPolls();
        List<Poll> munPolls = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start;
        Date end;

        for (Poll p : allPolls) {
            try {
                start = df.parse(p.startDate());
                end = df.parse(p.endDate());
            } catch (Exception e) {
                continue;
            }
            if (p.home().municipality().equals(muniregi.municipality()) && isPollDateInRange(start, end, pollEnum)) {
                munPolls.add(p);
            }
        }
        return munPolls;
    }
    public List<Poll> getRegPolls(MuniRegion muniregi) {
        return getRegPolls(muniregi, PollEnum.NonFinished);
    }
    public List<Poll> getRegPolls(MuniRegion muniregi, PollEnum pollEnum) {
        List<Poll> allPolls = db.getAllPolls();
        List<Poll> regPolls = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start;
        Date end;
        for (Poll p : allPolls) {
            try {
                start = df.parse(p.startDate());
                end = df.parse(p.endDate());
            } catch (Exception e) {
                continue;
            }
            if (p.home().region().equals(muniregi.region()) && isPollDateInRange(start, end, pollEnum)) {
                regPolls.add(p);
            }
        }
        return regPolls;
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
    public List<Poll> getEligiblePolls(Citizen c, PollEnum s) {
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
    public List<Poll> getEligiblePolls(Citizen c, PollEnum s, LevelEnum l){
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
    public List<Poll> getVotedPolls(Citizen c, PollEnum s){
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
    public List<Poll> getVotedPolls(Citizen c, PollEnum s, LevelEnum l){
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
    private static boolean isPollDateInRange(Date start, Date end, PollEnum ps){
        Date today = new Date();
        boolean inRange = false;
        switch (ps) {
            case Finished:
                if (today.after(end)) inRange = true;
                break;
            case NonFinished:
                if (end.after(today) || end.equals(today)) inRange = true;
                break;
            case Active:
                if ((end.after(today) || end.equals(today))
                &&  (start.equals(today) || start.before(today))) inRange = true;
                break;
            case Future:
                if (start.after(today)) inRange = true;
                break;
            default: inRange = false;
        }
        return inRange;
    }

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

    private boolean isPollDateInRange(Poll p, PollEnum s) {
        PollEnum _s;

        try {
            _s = getPollStatus(p);
        } catch (ParseException e) {
            return false;
        }

        if (s == null)
            return true;
        if (s == PollEnum.Finished && _s == PollEnum.Finished)
            return true;
        if (s == PollEnum.Future && _s == PollEnum.Future)
            return true;
        if (s == PollEnum.Active && _s == PollEnum.Active)
            return true;
        if (s == PollEnum.NonFinished && (_s == PollEnum.Active || _s == PollEnum.Future))
            return true;

        return false;
    }

    private PollEnum getPollStatus(Poll p) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date today = new Date();
        String start = p.startDate();
        String end = p.startDate();

        if(sdf.parse(end).before(today))
            return PollEnum.Finished;

        if (sdf.parse(start).after(today))
            return PollEnum.Future;

        return PollEnum.Active;
    }
    
}
