package agile18.demo.model.Exceptions;

public class PollAlreadyHasTopicException extends Exception {
    public PollAlreadyHasTopicException() {
        super("Poll already have this topic!");
    }
}
