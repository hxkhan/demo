package agile18.demo.model.Exceptions;

public class CitizenExistsException extends Exception {
    public CitizenExistsException() {
        super("Citizen with the specified person number already exists!");
    }
}
