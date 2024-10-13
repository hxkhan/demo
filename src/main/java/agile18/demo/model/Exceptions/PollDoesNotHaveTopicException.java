package agile18.demo.model.Exceptions;

public class PollDoesNotHaveTopicException extends Exception {
    public PollDoesNotHaveTopicException() {
        super("Poll does not have this topic!");
    }
}
