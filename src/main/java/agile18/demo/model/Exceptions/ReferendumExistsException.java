package agile18.demo.model.Exceptions;

public class ReferendumExistsException extends Exception {
    public ReferendumExistsException() {
        super("Refendum-ID already exists!");
    }
}
