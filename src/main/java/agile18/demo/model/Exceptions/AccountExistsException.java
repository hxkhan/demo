package agile18.demo.model.Exceptions;

public class AccountExistsException extends Exception {
    public AccountExistsException() {
        super("Account with the specified person number already exists!");
    }
}
