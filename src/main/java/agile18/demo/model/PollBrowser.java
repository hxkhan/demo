package agile18.demo.model;

import agile18.demo.model.Exceptions.PollDoesNotExistException;
import agile18.demo.model.Records.Poll;

import java.util.List;

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

    
}
