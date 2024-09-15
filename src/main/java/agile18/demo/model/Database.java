package agile18.demo.model;

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

    public User getUserWithPersonNumber(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = '" + id + "'';";

        // might work idk
        return jdbc.queryForObject(sql, User.class);
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
}
