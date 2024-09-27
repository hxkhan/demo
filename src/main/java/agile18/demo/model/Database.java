package agile18.demo.model;

//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.Utils;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Poll;

import java.util.*;

/*
    DATABASE CONTRACT: Everything passed to create functions must be correct and error checked!
    In case of wrong input, database will have undefined behaviour

    This includes:
        createCitizen
        createPoll

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
            result.get(0).get("municipality").toString(),
            result.get(0).get("region").toString()
        );
    }

    public boolean ifCitizenExists(String id) {
        return !jdbc.queryForList("SELECT 1 FROM Citizen WHERE id = '" + id + "';").isEmpty();
    }

    public List<Citizen> getAllCitizens() {
        return jdbc.query("SELECT * FROM Citizen;", (rs, rowNum) -> {
            return new Citizen(
                rs.getString("id"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("pass"),
                rs.getString("municipality"),
                rs.getString("region")
            );
        });
    }

    public List<String> getAllMunicipalities() {
        return jdbc.query("SELECT * FROM Municipality;", (r, rowNum) -> {
            return r.getString("name");
        });
    }

    public void createCitizen(String name, String id, String pass, String muni) throws CitizenExistsException {
        if (ifCitizenExists(id)) {
            throw new CitizenExistsException();
        }

        String sql = "INSERT INTO Citizen VALUES (" + Utils.sqlValues(id, name, pass, muni) + ");";
        jdbc.execute(sql);
    }

    // --------------------- Polls ---------------------
    public List<Poll> getAllPolls() {
        return jdbc.query("SELECT * FROM Poll;", (r, rowNum) -> {
            return new Poll(
                r.getInt("id"),
                r.getString("creator"),
                Level.valueOf(r.getString("level")),
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

    public void createPoll(Citizen creator, Level level, String title, String body, String startDate, String endDate) {
        String sql = "INSERT INTO Poll (creator, level, title, body, startDate, endDate, blank, favor, against) VALUES (" + 
            Utils.sqlValues(creator.id(), level, title, body, startDate, endDate, 0, 0, 0) + ");";

        jdbc.execute(sql);
    }

    public void castVote(Citizen voter, String pollId, VoteEnum vote) {

    }
}