package agile18.demo.model;

import org.springframework.stereotype.Service;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Municipality;

import java.util.*;

@Service
public class Onboarder {
    private final Database db;
    // map UUID -> personNr for login access tokens
    private HashMap<UUID, String> logins = new HashMap<UUID, String>();

    public Onboarder(Database db) {
        this.db = db;
    }

    public UUID register(String fname, String lname, String id, String pass, String homeMunicipality) throws CitizenExistsException, MunicipalityDoesNotExist {
        boolean found = false;
        for (Municipality city : db.getAllMunicipalities()) {
            if (city.name().equals(homeMunicipality)) {
                found = true;
                break;
            }
        }
        
        if (!found) throw new MunicipalityDoesNotExist();

        try {
            db.getCitizenWithPersonNumber(id);
            throw new CitizenExistsException();
        } catch (CitizenDoesNotExistException e) {
            db.createCitizen(fname, lname, id, pass, homeMunicipality);
            UUID uuid = java.util.UUID.randomUUID();
            logins.put(uuid, id);
            return uuid;
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
