package agile18.demo.model.Exceptions;

public class UnAuthorisedToVote extends Exception {
    public UnAuthorisedToVote() {
        super("Citizen is not authorised to vote there!");
    }
}
