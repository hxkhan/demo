package agile18.demo.model.Records;

import agile18.demo.model.LevelEnum;

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
    int against
) {}
