package agile18.demo.model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.Utils;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Municipality;
import agile18.demo.model.Records.Poll;

import java.util.*;

/*
    DATABASE CONTRACT: Everything passed to DB methods must be correct and error checked!
    In case of wrong input, database will have undefined behaviour

    This includes:
        createCitizen() for e.g. id must be of length 10 and user must NOT already exist in DB
            which means caller should first check so user does not exist with getCitizenWithPersonNumber()

        createPoll() for e.g. level cannot be null, and start/end dates must be in the correct format like 2024-10-14

    ALSO: Do not implement business logic in Database.java. This is supposed to be a CRUD object.
        That means (Create, Read, Update, Delete).
        If the user CAN or CAN NOT cast a vote is not up to the database,
            it is upto the caller of this object's methods and for them to ensure the rules are obeyed

*/

@Service
public class Database {
    private final JdbcTemplate jdbc;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // --------------------- Citizens ---------------------
    public Citizen getCitizenWithPersonNumber(String id) throws CitizenDoesNotExistException {
        String sql = "SELECT * FROM Citizen WHERE id = '" + id + "';";
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()) {
            throw new CitizenDoesNotExistException();
        }

        return new Citizen(
            result.get(0).get("id").toString(),
            result.get(0).get("firstName").toString(),
            result.get(0).get("lastName").toString(),
            result.get(0).get("pass").toString(),
            result.get(0).get("home").toString()
        );
    }

    public List<Citizen> getAllCitizens() {
        return jdbc.query("SELECT * FROM Citizen;", (rs, rowNum) -> {
            return new Citizen(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("pass"),
                rs.getString("home")
            );
        });
    }

    public List<Municipality> getAllMunicipalities() {
        return jdbc.query("SELECT * FROM Municipality;", (r, rowNum) -> {
            return new Municipality(r.getString("name"), r.getString("region"));
        });
    }

    public void createCitizen(String fname, String lname, String id, String pass, String home) {
        String sql = "INSERT INTO Citizen VALUES (" + Utils.sqlValues(id, fname, lname, pass, home) + ");";
        jdbc.execute(sql);
    }

    // --------------------- Polls ---------------------
    public List<Poll> getAllPolls() {
        return jdbc.query("SELECT * FROM Poll;", (r, rowNum) -> {
            return new Poll(
                r.getInt("id"),
                r.getString("home"),
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

    public Poll getPollWithID(int id) throws PollDoesNotExistException {
        var list = jdbc.query("SELECT * FROM Poll WHERE id = " + id + ";", (r, rowNum) -> {
            return new Poll(
                r.getInt("id"),
                r.getString("home"),
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

        if (list.isEmpty()) throw new PollDoesNotExistException();
        return list.get(0);
    }

    public void createPoll(Citizen creator, LevelEnum level, String title, String body, String startDate, String endDate) {
        String values = Utils.sqlValues(creator.home(), level.toString(), title, body, startDate, endDate, 0, 0, 0);
        String sql = "INSERT INTO Poll (home, level, title, body, startDate, endDate, blank, favor, against) VALUES (" + values + ");";
        jdbc.execute(sql);
    }

    public boolean canCast(Citizen voter, int poll) {
        String sql = "SELECT 1 FROM Casted WHERE voter = '" + voter.id() + "' AND poll = " + poll + ";";
        return jdbc.queryForList(sql).isEmpty();
    }

    // let the caller of castVote() call canCast() and implement the business logic
    // remember, just CRUD
    public void castVote(Citizen voter, int poll, VoteEnum vote) {
        // cast a vote
        jdbc.execute("INSERT INTO Casted VALUES (" + Utils.sqlValues(voter.id(), poll) + ");");

        String column = switch(vote) {
            case Favor -> "favour";
            case Against -> "against";
            case Blank -> "blank";
        };

        // update poll
        jdbc.execute("UPDATE Polls SET " + column + " = " + column + " + 1;");
    }
}