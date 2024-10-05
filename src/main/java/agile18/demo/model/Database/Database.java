package agile18.demo.model.Database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

import agile18.Utils;
import agile18.demo.model.*;
import agile18.demo.model.Records.*;

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

        return getPolls(sql);
    }
    public List<Poll> getAllPollsFilter(PollStatusEnum s, LevelFilterEnum l) {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+
                "FROM Poll p JOIN Municipality m ON p.home = m.name";
        sql = addSQLFilters(sql, s, l) + ";";

        return getPolls(sql);
    }
    public List<Poll> getMuniPolls(String m, PollStatusEnum s){
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+
                "FROM Poll p JOIN Municipality m ON p.home = m.name WHERE municipality = " + m;
        if (s != PollStatusEnum.All)
            sql = sql + " AND ";
        sql = addStatusFilter(sql, s) + ";";
        return getPolls(sql);
    }
    public List<Poll> getEligiblePollsFilter(Citizen c, PollStatusEnum s, LevelFilterEnum l) {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+
                "FROM Poll p JOIN Municipality m ON p.home = m.name";

        return getPolls(sql);
    }
    public List<Poll> getVotedPollsFilter(Citizen c, PollStatusEnum s, LevelFilterEnum l) {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+
                "FROM Poll p JOIN Municipality m ON p.home = m.name;";

        return getPolls(sql);
    }
    private List<Poll> getPolls(String sql){
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
    private String addSQLFilters(String sql, PollStatusEnum s, LevelFilterEnum l){
        if (s != PollStatusEnum.All || l != LevelFilterEnum.All)
            sql = sql + " WHERE ";

        sql = addStatusFilter(sql, s);
        if (l != LevelFilterEnum.All)
            sql = sql + " AND ";

        return addLevelFilter(sql, l);
    }
    private String addStatusFilter(String sql, PollStatusEnum s) {
        return switch(s) {
            case All -> sql;
            case Passed -> sql + "endDate < CURRENT_DATE";
            case Active -> sql + "endDate >= CURRENT_DATE AND startDate <= CURRENT_DATE";
            case Future -> sql + "startDate > CURRENT_DATE";
            case NotPassed -> sql + "endDate >= CURRENT_DATE";
        };
    }
    private String addLevelFilter(String sql, LevelFilterEnum l){
        return switch(l) {
            case All -> sql;
            case Municipal -> sql + "level = Municipal";
            case Regional -> sql + "level = Regional";
            case National -> sql + "level = National";
        };
    }

    public Poll getPollWithID(int id) {
        String sql = "SELECT id, creator, home AS municipality, region, level, title, body, startDate, endDate, blank, favor, against "+ 
            "FROM Poll p JOIN Municipality m ON p.home = m.name WHERE id = " + id + ";";

        var list = getPolls(sql);

        if (list.isEmpty()) return null;
        return list.getFirst();
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


    // ROLES
    // implement roles during sat/sun


    // NEWS------------------------------
    public int postNews(String title, String body, String date) {
        int id = jdbc.queryForObject("SELECT COUNT(*) FROM News;", Integer.class);

        String values = Utils.sqlValues(id, title, body, date);
        String sql = "INSERT INTO News VALUES (" + values + ");";
        jdbc.execute(sql);
        return id;
    }

    public List<NewsPost> getAllNews() {
        String sql = "SELECT n.title, n.body, n.date, " +
                 "SUM(CASE WHEN co.favorable THEN 1 ELSE 0 END) AS favorable, " +
                 "SUM(CASE WHEN NOT co.favorable THEN 1 ELSE 0 END) AS unfavorable " +
                 "FROM News n " +
                 "LEFT JOIN CastedOpinion co ON n.id = co.newsId " +
                 "GROUP BY n.id " +
                 "ORDER BY n.date DESC;";

        return jdbc.query(sql, (r, rowNum) -> {
            return new NewsPost(
                r.getString("title"),
                r.getString("body"),
                r.getString("date"),
                r.getInt("favorable"),
                r.getInt("unfavorable")
            );
        });
    }

}