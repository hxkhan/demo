package agile18.demo.model;

//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.*;

import java.util.*;

@Service
public class Database {
    private final JdbcTemplate jdbc;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public Citizen getCitizenWithPersonNumber(String id) throws CitizenDoesNotExistException {
        String sql = "SELECT * FROM Citizens WHERE id = '" + id + "';";
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()) {
            throw new CitizenDoesNotExistException();
        }
        Citizen user = new Citizen(
                result.get(0).get("id").toString(),
                result.get(0).get("name").toString(),
                result.get(0).get("pass").toString());
        return user;
    }

    public boolean ifCitizenExists(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "';";
        return !jdbc.queryForList(sql).isEmpty();
    }

    public List<Citizen> getAllCitizens() {
        String sql = "SELECT * FROM Citizens;";

        // Query for a list of rows
        List<Citizen> entities = jdbc.query(sql, (rs, rowNum) -> {
            return new Citizen(rs.getString("id"), rs.getString("name"), rs.getString("pass"));
        });

        return entities;
    }

    public void createCitizen(String name, String id, String pass) throws CitizenExistsException {
        if (ifCitizenExists(id)) {
            throw new CitizenExistsException();
        }
        String sql = "INSERT INTO Citizens VALUES ('" + id + "', '" + name + "', '" + pass + "');";
        jdbc.execute(sql);
    }

    // Not used anymore -------------------------------------------------------
    /*
     * public User getUserWithPersonNumberX(String id) {
     * String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "';";
     * // might work idk
     * User me = new User("hej", "då");
     * return me;// jdbc.queryForObject(sql, User.class);
     * }
     */
}