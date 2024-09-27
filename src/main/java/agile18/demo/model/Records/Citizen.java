package agile18.demo.model.Records;

// 1:1 representation of a Citizen in database
public record Citizen(String id, String firstName, String lastName, String pass, String municipality, String region) {}
