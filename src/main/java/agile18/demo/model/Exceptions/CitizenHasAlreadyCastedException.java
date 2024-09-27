package agile18.demo.model.Exceptions;

public class CitizenHasAlreadyCastedException extends Exception {
    public CitizenHasAlreadyCastedException() {
        super("This citizen has already casted a vote in this poll!");
    }
}
