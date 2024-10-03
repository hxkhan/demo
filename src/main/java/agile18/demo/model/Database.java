package agile18.demo.model;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.Utils;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.MuniRegion;
import agile18.demo.model.Records.Poll;

/*
    DATABASE CONTRACT: Everything passed to DB methods is to be expected to be correct and error checked!
    Use NO exceptions from now on!
    In case of wrong input, database is entitled to have undefined behaviour, its the job of the caller to use the DB correctly

    This includes:
        createCitizen() for e.g. id must be of length 10 and user must NOT already exist in DB
            which means caller should first check so user does not exist with getCitizenWithPersonNumber()

        createPoll() for e.g. level cannot be null, and start/end dates must be in the correct format like 2024-10-14

    ALSO: Do not implement business logic in Database.java. This is supposed to be a CRUD object.
        That means (Create, Read, Update, Delete).
        If the user CAN or CAN NOT cast a vote is not up to the database,
            it is upto the caller of this object's methods and for them to ensure the rules are obeyed
            thus sufficient methods like getPollWithID() or hasCast() need to be provided to the caller of database 
            so it can check all of the prerequisites and requirements

    NO exceptions & NO error checking!
*/

@Service
public class Database {
    private final JdbcTemplate jdbc;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // --------------------- Citizens ---------------------
    public Citizen getCitizenWithPersonNumber(String id) {
        String sql = "SELECT id, firstName, lastName, pass, home AS municipality, region " +
            "FROM Citizen c JOIN Municipality m ON c.home = m.name WHERE id = '" + id + "';";

        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()) return null;

        return new Citizen(
            result.get(0).get("id").toString(),
            result.get(0).get("firstName").toString(),
            result.get(0).get("lastName").toString(),
            result.get(0).get("pass").toString(),
            new MuniRegion(
                result.get(0).get("municipality").toString(),
                result.get(0).get("region").toString()
            )
        );
    }

    public List<Citizen> getAllCitizens() {
        String sql = "SELECT id, firstName, lastName, pass, home AS municipality, region " +
            "FROM Citizen c JOIN Municipality m ON c.home = m.name;";
        return jdbc.query(sql, (rs, rowNum) -> {
            return new Citizen(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("pass"),
                new MuniRegion(
                    rs.getString("municipality"),
                    rs.getString("region")
                )
            );
        });
    }

    public List<MuniRegion> getAllMunicipalities() {
        return jdbc.query("SELECT * FROM Municipality;", (r, rowNum) -> {
            return new MuniRegion(r.getString("name"), r.getString("region"));
        });
    }

    public void createCitizen(String fname, String lname, String id, String pass, String home) {
        String sql = "INSERT INTO Citizen VALUES (" + Utils.sqlValues(id, fname, lname, pass, home) + ");";
        jdbc.execute(sql);
    }

    // --------------------- Polls ---------------------
    public List<Poll> getAllPolls() {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+ 
            "FROM Poll p JOIN Municipality m ON p.home = m.name;";

        return jdbc.query(sql, (r, rowNum) -> {
            return new Poll(
                r.getInt("id"),
                r.getString("creator"),
                new MuniRegion(r.getString("municipality"), r.getString("region")),
                LevelEnum.valueOf(r.getString("level")),
                r.getString("title"),
                r.getString("body"),
                r.getString("startDate"),
                r.getString("endDate"),
                r.getInt("blank"),
                r.getInt("favor"),
                r.getInt("against")
            );
        });
    }

    public Poll getPollWithID(int id) {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+ 
            "FROM Poll p JOIN Municipality m ON p.home = m.name WHERE id = " + id + ";";

        var list = jdbc.query(sql, (r, rowNum) -> {
            return new Poll(
                r.getInt("id"),
                r.getString("creator"),
                new MuniRegion(r.getString("municipality"), r.getString("region")),
                LevelEnum.valueOf(r.getString("level")),
                r.getString("title"),
                r.getString("body"),
                r.getString("startDate"),
                r.getString("endDate"),
                r.getInt("blank"),
                r.getInt("favor"),
                r.getInt("against")
            );
        });

        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public int createPoll(Citizen creator, LevelEnum level, String title, String body, String startDate, String endDate) {
        int id = jdbc.queryForObject("SELECT COUNT(*) FROM Poll;", Integer.class);
        String values = Utils.sqlValues(id, creator.id(), creator.home().municipality(), level.toString(), title, body, startDate, endDate, 0, 0, 0);
        String sql = "INSERT INTO Poll VALUES (" + values + ");";
        jdbc.execute(sql);
        return id;
    }
    public void removePoll(int id){
        String sql = "DELETE FROM Poll WHERE id = " + id + ";";
        jdbc.execute(sql);
    }

    public boolean hasCast(Citizen voter, int poll) {
        String sql = "SELECT 1 FROM Casted WHERE voter = '" + voter.id() + "' AND poll = " + poll + ";";
        return !jdbc.queryForList(sql).isEmpty();
    }

    // let the caller of castVote() call hasCast() and implement the business logic
    // remember, just CRUD..
    // the business logic is in PollingStation.java
    public void castVote(Citizen voter, int poll, VoteEnum vote) {
        String column = switch(vote) {
            case Favor -> "favor";
            case Against -> "against";
            case Blank -> "blank";
        };

        String sql = "BEGIN TRANSACTION;\n" + 
            "INSERT INTO Casted VALUES (" + Utils.sqlValues(voter.id(), poll) + ");" +
            "UPDATE Poll SET " + column + " = " + column + " + 1 WHERE id = " + poll + ";\n" +
            "COMMIT;";


        // vote
        jdbc.execute(sql);
    }
}