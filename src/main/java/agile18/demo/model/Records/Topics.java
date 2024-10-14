package agile18.demo.model.Records;
import java.util.ArrayList;
import java.util.List;

public record Topics (
    List<Integer> Economy,
    List<Integer> Climate,
    List<Integer> Healthcare,
    List<Integer> Security,
    List<Integer> Education
){
    public static Topics defaults() {
        return new Topics(
            new ArrayList<Integer>(),
            new ArrayList<Integer>(),
            new ArrayList<Integer>(),
            new ArrayList<Integer>(),
            new ArrayList<Integer>()
            );
    }
}
