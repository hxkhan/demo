package agile18.demo.model;

import org.springframework.stereotype.Service;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.MuniRegion;

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
        for (MuniRegion city : db.getAllMunicipalities()) {
            if (city.municipality().equals(homeMunicipality)) {
                found = true;
                break;
            }
        }
        
        if (!found) throw new MunicipalityDoesNotExist();

        Citizen c = db.getCitizenWithPersonNumber(id);
        if (c != null) throw new CitizenExistsException();
            
        db.createCitizen(fname, lname, id, pass, homeMunicipality);
        UUID uuid = java.util.UUID.randomUUID();
        logins.put(uuid, id);
        return uuid;
    }

    public UUID login(String id, String password) throws CitizenDoesNotExistException, IncorrectPasswordException {
        Citizen c = db.getCitizenWithPersonNumber(id);
        if (c == null) throw new CitizenDoesNotExistException();

        if (!c.pass().equals(password)) throw new IncorrectPasswordException();

        UUID uuid = java.util.UUID.randomUUID();
        logins.put(uuid, id);
        return uuid;
    }

    public Citizen checkLogin(UUID uuid) throws NotLoggedInException {
        if (!logins.containsKey(uuid)) throw new NotLoggedInException();
        String id = logins.get(uuid); 
        return db.getCitizenWithPersonNumber(id);
    }

    // debug purposes
    public Citizen getCitizenWithPersonNumber(String id) throws CitizenDoesNotExistException {
        return db.getCitizenWithPersonNumber(id);
    }

    // debug purposes
    public List<Citizen> getAllCitizens() {
        return db.getAllCitizens();
    }
}
