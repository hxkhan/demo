package agile18.demo.model;

import org.springframework.stereotype.Service;
import agile18.demo.model.Exceptions.*;

import java.util.*;

@Service
public class Onboarder {
    private final Database db;
    // map UUID -> personNr for login access tokens
    private HashMap<UUID, String> logins = new HashMap<UUID, String>();

    public Onboarder(Database db) {
        this.db = db;
        // just for test
        db.testReferendum();
    }

    public UUID register(String name, String id, String pass) throws CitizenExistsException {
        try {
            db.createCitizen(name, id, pass);

            UUID uuid = java.util.UUID.randomUUID();
            logins.put(uuid, id);
            return uuid;

        } catch (CitizenExistsException e) {
            throw e;
        }
    }

    public UUID login(String id, String password) throws CitizenDoesNotExistException, IncorrectPasswordException {
        try {
            Citizen user = db.getCitizenWithPersonNumber(id);
            if (!user.pass().equals(password)) throw new IncorrectPasswordException();

            UUID uuid = java.util.UUID.randomUUID();
            logins.put(uuid, id);
            return uuid;
        } catch (CitizenDoesNotExistException e) {
            throw e;
        }
    }

    public Citizen checkLogin(UUID uuid) throws NotLoggedInException {
        if (!logins.containsKey(uuid)) throw new NotLoggedInException();
        String id = logins.get(uuid); 

        try {
            return db.getCitizenWithPersonNumber(id);
        } catch (CitizenDoesNotExistException e) {
            throw new RuntimeException("Citizen went extinct from database after login, what are we even doing?");
        }
    }

    // also for debug purposes for now
    public Citizen getCitizenWithPersonNumber(String id) throws CitizenDoesNotExistException {
        return db.getCitizenWithPersonNumber(id);
    }

    // remove later; only exists to show the functionality
    public List<Citizen> getAllCitizens() {
        return db.getAllCitizens();
    }
}
