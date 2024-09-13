package agile18.demo.model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Database {
    private final JdbcTemplate jdbcTemplate;

    public Database(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> getAllCitizens() {
        String sql = "SELECT * FROM Citizens;";
        
        // Query for a list of rows
        List<User> entities = jdbcTemplate.query(sql, (rs, rowNum) -> {
            User entity = new User(rs.getString("id"), rs.getString("name"));
            return entity;
        });

        return entities;

        // Query for a single value
        // jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Citizens", Integer.class);
        
        // Execute an update
        //jdbcTemplate.update("INSERT INTO Citizens VALUES (?)", "New Name");
    }
}
