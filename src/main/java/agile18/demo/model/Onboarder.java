package agile18.demo.model;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.*;

import java.util.*;

/* rough api of this class for now
interface {
    UUID register(name, personNr, password)
        will attempt to register with the info and do the same as login
    UUID login(personNr, password)
        will attempt to login with the credentials and generate a UUID which is saved as logins[UUID] = personNr
    User checkLogin(UUID)
        checks if the UUID is assigned a user and if yes then return the user else throw exception
}
*/

@Service
public class Onboarder {
    private final JdbcTemplate jdbc;
    // map UUID -> personNr for login access tokens
    private static HashMap<UUID, String> logins = new HashMap<UUID, String>();

    public Onboarder(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public User checkLogin(UUID id) throws NotLoggedInException {
        if (!logins.containsKey(id)) throw new NotLoggedInException();
        String personNr = logins.get(id); 
        return getUserWithPersonNumber(personNr);
    }

    public User getUserWithPersonNumber(String id) {
        String sql = "SELECT 1 FROM Citizens WHERE id = " + id + ";";

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
