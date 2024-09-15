package agile18.demo.model.Exceptions;

public class WrongPasswordException extends Exception {
    public WrongPasswordException() {
        super("Wrong password!");
    }
}
