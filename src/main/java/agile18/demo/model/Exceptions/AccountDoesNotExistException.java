package agile18.demo.model.Exceptions;

public class AccountDoesNotExistException extends Exception {
    public AccountDoesNotExistException() {
        super("No account with the specified person number exists!");
    }
}
