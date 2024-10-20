package agile18.demo.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import agile18.Utils;
import agile18.demo.model.NewsStation;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.NewsComment;
import agile18.demo.model.Records.NewsPost;

@RestController
public class NewsController {
    private final NewsStation ns;

    public NewsController(NewsStation ns) {
        this.ns = ns;
    }

    @PostMapping("/favor-news")
    public Map<String, Object> onFavorNews(@RequestParam String uuid, @RequestBody BodyOfFavorNews body) {
        try {
            System.out.println("favor:" + body.favorable() + "; id:" + body.newsId());
            ns.favorNews(body.newsId(), UUID.fromString(uuid), body.favorable());
            return Map.of(
                    "success", true);
        } catch (NotLoggedInException e) {
            return Map.of(
                    "success", false,
                    "message", "Not logged in");
        }
    }

    @PostMapping("/post-news")
    public Map<String, Object> onPostNews(@RequestParam String uuid, @RequestBody BodyOfPostNews body) {
        if (!body.isValid()) {
            return Map.of(
                    "success", false,
                    "message", "invalid request body");
        }

        try {
            ns.postNews(UUID.fromString(uuid), body.title(), body.body());
            return Map.of(
                    "success", true);
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", "invalid uuid");
        } catch (NotLoggedInException e) {
            return Map.of(
                    "success", false,
                    "message", "not logged in");
        }
    }

    @GetMapping("/news")
    public Map<String, Object> onGetNews(@RequestParam String uuid) {
        try {
            return Map.of(
                    "success", true,
                    "news", ns.getAllNews(),
                    "isSecretary", ns.isSecretary(UUID.fromString(uuid)));
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "message", "invalid uuid");
        } catch (NotLoggedInException e) {
            return Map.of(
                    "success", false,
                    "message", "not logged in");
        }
    }

    @GetMapping("/single-news")
    public NewsPost onGetSingleNews(@RequestParam int news) {
        return ns.getSingleNews(news);
    }

    @GetMapping("/news-comments")
    public List<NewsComment> onGetNewsComments(@RequestParam int news) {
        return ns.getNewsComments(news);
    }

    @PostMapping("/post-comment")
    public Map<String, Object> onPostComment(@RequestParam String uuid, @RequestBody BodyOfPostComment body) {
        try {
            ns.postComment(UUID.fromString(uuid), body.newsId(), body.comment());
            return Map.of(
                    "success", true);
        } catch (NotLoggedInException e) {
            return Map.of(
                    "success", false,
                    "message", "not logged in");
        }
    }
    
    @GetMapping("/get-icon")
    public Map<String, Object> onGetIcon(@RequestParam String uuid) {
        try {
            String icon = ns.getIcon(uuid);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", icon);
            return response;
        } catch (NotLoggedInException e) {
            return Map.of(
                        "success", false,
                        "message", "not logged in");
        }
    }

    @PostMapping("/post-icon")
    public Map<String, Object> onPostIcon(@RequestParam String uuid, @RequestBody MultipartFile icon) {
        try {
            ns.postIcon(uuid, icon);
            return Map.of(
                        "success", true,
                        "message", "successfully uploaded icon");
        } catch (NotLoggedInException e) {
            return Map.of(
                        "success", false,
                        "message", "not logged in");
        } catch (IOException e) {
            return Map.of(
                        "success", false,
                        "message", "error uploading image");
        }
    }
}

record BodyOfPostNews(String title, String body) {
    boolean isValid() {
        return !Utils.isEmpty(title, body);
    }
}

record BodyOfFavorNews(int newsId, boolean favorable) {
    boolean isValid() {
       return true;// return newsId != null && favorable != null
    }
}

record BodyOfPostComment(int newsId, String comment) {
}