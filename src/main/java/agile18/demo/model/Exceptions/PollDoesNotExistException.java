package agile18.demo.model.Exceptions;

public class PollDoesNotExistException extends Exception  {
    public PollDoesNotExistException() {
        super("No poll with the specified ID exists!");
    }
}