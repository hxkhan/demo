package agile18.demo.model;

import org.springframework.stereotype.Service;

import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.NewsPost;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NewsStation {
    private final Database db;
    private final Onboarder ob;

    public NewsStation(Database db, Onboarder ob) {
        this.db = db;
        this.ob = ob;
    }

    public void postNews(UUID accessToken, String title, String body) throws NotLoggedInException {
        Citizen c = ob.checkLogin(accessToken);
        LocalDateTime cdt = LocalDateTime.now();
        String date = cdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        db.postNews(title, body, date);
    }

    public List<NewsPost> getAllNews() {
        return db.getAllNews();
    }

}
