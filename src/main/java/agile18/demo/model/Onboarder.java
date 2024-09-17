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
    private final Database db;

    public Onboarder(JdbcTemplate jdbcTemplate, Database db) {
        this.jdbc = jdbcTemplate;
        this.db = db;

        /*
        try {
            System.out.println(db.getUserWithPersonNumber("0101010212"));

        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            this.db.createCitizen("Hej","0101010212","ewewe");
        } catch (Exception e) {
            System.out.println(e);
        }
    
        try {
            System.out.println(db.loginUser("0101010212", "ewewe"));

        } catch (Exception e) {
            System.out.println(e);
        }
        */
    }

    public User checkLogin(UUID id) throws NotLoggedInException, AccountDoesNotExistException {
        if (!logins.containsKey(id)) throw new NotLoggedInException();
        String personNr = logins.get(id); 
        return db.getUserWithPersonNumber(personNr);
    }

    // remove later; only exists to show the functionality
    public List<User> getAllCitizens() {
        return db.getAllCitizens();
    }
}
