package agile18.demo.model.Records;

import agile18.demo.model.Level;

// 1:1 representation of a Referendum in database; add the rest later
public record Poll(
    int id,
    String creator,
    Level level,
    String title,
    String body,
    String startDate,
    String endDate,
    int blank,
    int favor,
    int against
) {}
