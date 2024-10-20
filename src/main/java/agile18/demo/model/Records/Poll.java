package agile18.demo.model.Records;

import agile18.demo.model.LevelEnum;

import java.util.*;

public record Poll(
    int id,
    String creator,
    MuniRegion home,
    LevelEnum level,
    String title,
    String body,
    String startDate,
    String endDate,
    int blank,
    int favor,
    int against,
    List<String> topics
) {

    public Poll withTopics(List<String> t) {
        return new Poll(id, creator, home, level, title, body, startDate, endDate, blank, favor, against, t);
    }
}
