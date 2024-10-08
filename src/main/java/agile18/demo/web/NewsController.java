package agile18.demo.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

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
            ns.favorNews(body.newsId(), UUID.fromString(uuid), body.favorable());
            return Map.of(
                "success", true);
        }
        catch (NotLoggedInException e) {
            return Map.of(
                "success", false,
                "message", "Not logged in"
            );
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
    public List<NewsPost> onGetNews() {
        return ns.getAllNews();
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
}

record BodyOfPostNews(String title, String body) {
    boolean isValid() {
        return !Utils.isEmpty(title, body);
    }
}

record BodyOfFavorNews(int newsId, boolean favorable) {}

record BodyOfPostComment(int newsId, String comment) {}