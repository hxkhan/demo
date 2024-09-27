package agile18.demo.model.Records;

// 1:1 representation of a Referendum in database; add the rest later
public record Referendum(int id, String area, String title, String body, String startDate, String endDate) {}
