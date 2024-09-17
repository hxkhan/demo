package agile18.demo.model.Exceptions;

public class IncorrectPasswordException extends Exception {
    public IncorrectPasswordException() {
        super("Incorrect passord provided!");
    }
}
