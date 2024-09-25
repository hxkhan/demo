package agile18.demo.model.Exceptions;

public class ReferendumNotFoundException extends Exception  {
    public ReferendumNotFoundException() {
        super("No results found for referendum ID!");
    }
}