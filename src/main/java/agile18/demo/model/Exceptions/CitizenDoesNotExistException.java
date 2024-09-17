package agile18.demo.model.Exceptions;

public class CitizenDoesNotExistException extends Exception {
    public CitizenDoesNotExistException() {
        super("No citizen with the specified person number exists!");
    }
}
