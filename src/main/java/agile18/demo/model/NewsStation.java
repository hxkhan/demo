package agile18.demo.model;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import agile18.demo.model.Database.Database;
import agile18.demo.model.Exceptions.NotLoggedInException;
import agile18.demo.model.Records.*;
import java.util.*;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    public boolean isSecretary(UUID accessToken) throws NotLoggedInException {
        String citizenId = ob.checkLogin(accessToken).id();
        return db.isSecretary(citizenId);
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

    public void postComment(UUID accessToken, int newsId, String comment) throws NotLoggedInException {
        String citizenId = ob.checkLogin(accessToken).id();
        LocalDateTime cdt = LocalDateTime.now();
        String date = cdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        db.postComment(newsId, citizenId, comment, date);
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

    public String getIcon(String uuid) throws NotLoggedInException{
        Citizen c = ob.checkLogin(UUID.fromString(uuid));
        String citizenId = c.id();
        if (db.hasIcon(citizenId)) return db.getIcon(c.id());
        return null;
    }

    public void postIcon(String uuid, MultipartFile icon) throws NotLoggedInException, IOException {
        Citizen c = ob.checkLogin(UUID.fromString(uuid));

        BufferedImage originalImage = ImageIO.read(icon.getInputStream());
        BufferedImage resizedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 50, 50, null);
        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", baos);

        byte[] iconBytes = baos.toByteArray();
        String iconStr = Base64.getEncoder().encodeToString(iconBytes);
        if (db.hasIcon(c.id())) db.deleteIcon(c.id());
        db.postIcon(c.id(), iconStr);
    }
}
