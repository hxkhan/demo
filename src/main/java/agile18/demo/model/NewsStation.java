package agile18.demo.model;

import org.springframework.stereotype.Service;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Records.*;
import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NewsStation {
    private final Database db;
    private final Onboarder ob;

    public NewsStation(Database db, Onboarder ob) {
        this.db = db;
        this.ob = ob;
    }

    public void postNews(UUID accessToken, String title, String body) throws NotLoggedInException {
        String citizenId = ob.checkLogin(accessToken).id();
        if (db.isSecretary(citizenId)) {
            LocalDateTime cdt = LocalDateTime.now();
            String date = cdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            db.postNews(title, body, date);
        }
    }

    public List<NewsPost> getAllNews() {
        return db.getAllNews();
    }

    public NewsPost getSingleNews(int newsId) {
        return db.getSingleNews(newsId);
    }

    public List<NewsComment> getNewsComments(int newsId) {
        return db.getNewsComments(newsId);
    }

    public void favorNews(int newsId, UUID accessToken, boolean favorable) throws NotLoggedInException {
        String citizenId = ob.checkLogin(accessToken).id();
        if (db.hasFavoredNews(newsId, citizenId) && db.isFavorableNewsFavor(newsId, citizenId) == favorable) {
            db.deleteNewsFavor(newsId, citizenId);
        }
        else if (db.hasFavoredNews(newsId, citizenId)) {
            db.changeNewsFavor(newsId, citizenId, favorable);
        }
        else {
            db.favorNews(newsId, citizenId, favorable);
        }
    }

}
