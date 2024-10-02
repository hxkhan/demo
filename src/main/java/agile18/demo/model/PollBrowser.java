package agile18.demo.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Records.MuniRegion;
import agile18.demo.model.Records.Poll;

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
        return getMunPolls(muniregi, PollStatus.NonFinished);
    }
    public List<Poll> getMunPolls(MuniRegion muniregi, PollStatus pollStatus) {
        List<Poll> allPolls = db.getAllPolls();
        List<Poll> munPolls = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start;
        Date end;

        for (Poll p : allPolls) {
            try {
                start = df.parse(p.startDate());
                System.out.println("start: " + start);
                end = df.parse(p.endDate());
                System.out.println("end: " + end);
            } catch (Exception e) {
                continue;
            }
            if (p.home().municipality().equals(muniregi.municipality()) && isPollDateInRange(start, end, pollStatus)) {
                munPolls.add(p);
            }
        }
        return munPolls;
    }
    public List<Poll> getRegPolls(MuniRegion muniregi) {
        return getRegPolls(muniregi, PollStatus.NonFinished);
    }
    public List<Poll> getRegPolls(MuniRegion muniregi, PollStatus pollStatus) {
        List<Poll> allPolls = db.getAllPolls();
        System.out.println(allPolls);
        List<Poll> regPolls = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date start;
        Date end;
        for (Poll p : allPolls) {
            try {
                start = df.parse(p.startDate());
                end = df.parse(p.endDate());
            } catch (Exception e) {
                System.out.println(e);
                continue;
            }           
            if (p.home().region().equals(muniregi.region()) && isPollDateInRange(start, end, pollStatus)) {
                regPolls.add(p);
            }
        }
        return regPolls;
    }

    // ----- HELPERS -----
    private static boolean isPollDateInRange(Date start, Date end,PollStatus ps){
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
    
}
