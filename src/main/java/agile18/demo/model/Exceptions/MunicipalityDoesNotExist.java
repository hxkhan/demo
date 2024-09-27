package agile18.demo.model.Exceptions;

public class MunicipalityDoesNotExist extends Exception {
    public MunicipalityDoesNotExist() {
        super("No municipality with the specified name exists");
    }
}
