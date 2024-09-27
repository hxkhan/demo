package agile18.demo.model;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Exceptions.ReferendumExistsException;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Referendum;

@Service
public class PollingStation {
    private final Database db;
    private final Onboarder ob;

    public PollingStation(Database db, Onboarder ob) {
        this.db = db;
        this.ob = ob;
    }

    public List<Referendum> getAllReferendums() {
        return db.getAllReferendums();
    }

    public void createReferendum(UUID accessToken, String title, String body, String level, String startDate, String endDate) throws NotLoggedInException {
        try {
            Citizen creator = ob.checkLogin(accessToken);
            String id = db.getUniqueRefId();
            db.createReferendum(id, creator.municipality(), title, body, startDate, endDate);
        } catch (ReferendumExistsException e) {
            // This should not happen!! Use AUTO_INCREMENT in sql
        }
    }
}
