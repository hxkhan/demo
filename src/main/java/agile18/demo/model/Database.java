package agile18.demo.model;

import org.springframework.dao.EmptyResultDataAccessException;
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

    // This is our getCitizen, returns a user. Is citizen supposed to be represented as a user in java?
    public User getUserWithPersonNumber(String id) throws AccountDoesNotExistException {
        String sql = "SELECT * FROM Citizens WHERE id = '" + id + "';";
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()){
            throw new AccountDoesNotExistException();
        } 
        User user = new User(result.get(0).get("ID").toString(), result.get(0).get("NAME").toString());
        return user;
    }
    public User loginUser(String id, String pass) throws WrongPasswordException, AccountDoesNotExistException{
        String sql = "SELECT * FROM Citizens WHERE id = '" + id + "';";
        List<Map<String, Object>> result = jdbc.queryForList(sql);
        if (result.isEmpty()){
            throw new AccountDoesNotExistException();
        } 
        if (!pass.equals(result.get(0).get("pass").toString())){
            throw new WrongPasswordException();
        }
        User user = new User(result.get(0).get("ID").toString(), result.get(0).get("NAME").toString());
        return user;
    }

    public boolean checkIfUserExists(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "';";
        return !jdbc.queryForList(sql).isEmpty();
    }

    // remove later; only exists to show the functionality
    public List<User> getAllCitizens() {
        String sql = "SELECT * FROM Citizens;";
        
        // Query for a list of rows
        List<User> entities = jdbc.query(sql, (rs, rowNum) -> {
            User entity = new User(rs.getString("id"), rs.getString("name"));
            return entity;
        });

        return entities;
        // Execute an update example
        //jdbcTemplate.update("INSERT INTO Citizens VALUES (?)", "New Name");
    }
    public void createCitizen(String name, String personNR, String pass) throws AccountExistsException {
        if (checkIfUserExists(personNR)) {
            throw new AccountExistsException();
        }
        String sql = "INSERT INTO Citizens VALUES ('" + personNR + "', '" + name + "', '" + pass + "');";
        jdbc.execute(sql);
    }


    // Not used anymore -------------------------------------------------------
    
    public User getUserWithPersonNumberX(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "';";
        // might work idk
        User me = new User("hej", "då");
        return me;//jdbc.queryForObject(sql, User.class);
    }
}