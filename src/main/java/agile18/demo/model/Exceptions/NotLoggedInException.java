package agile18.demo.model.Exceptions;

public class NotLoggedInException extends Exception {
    public NotLoggedInException() {
        super("The provided UUID is not logged in!");
    }
}
