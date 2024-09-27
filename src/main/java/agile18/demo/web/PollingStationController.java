package agile18.demo.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import agile18.Utils;
import agile18.demo.model.LevelEnum;
import agile18.demo.model.PollingStation;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Poll;

@RestController
public class PollingStationController {
    private final PollingStation ps;

    public PollingStationController(PollingStation ps) {
        this.ps = ps;
    }

    @PostMapping("/create-poll")
    public Map<String, Object> onCreatePoll(@RequestParam String uuid, @RequestBody BodyOfCreatePoll body) {
        if (!body.isValid()) {
            return Map.of(
                "success", false,
                "message", "invalid request body"
            );
        }

        try {
            ps.createPoll(UUID.fromString(uuid), body.title(), body.body(), LevelEnum.valueOf(body.level()),  body.startDate(), body.endDate());
            return Map.of(
                "success", true
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                "success", false,
                "message", "invalid uuid"
            );
        } catch (NotLoggedInException e) {
            return Map.of(
                "success", false,
                "message", "not logged in"
            );
        }
    }

    @GetMapping("/polls")
    public List<Poll> onGetPolls() {
        return ps.getAllPolls();
    }

    @GetMapping("/poll")
    public Map<String, Object>  onGetPoll(@RequestParam int id) {
        try {
            return Map.of(
                "success", true,
                "data", ps.getPollWithID(id)
            );
        } catch (PollDoesNotExistException e) {
            return Map.of(
                "success", false,
                "message", "poll does not exist"
            );
        }
    }
}


record BodyOfCreatePoll(String title, String body, String level, String startDate, String endDate) {
    boolean isValid() {
        return !Utils.isEmpty(title, body, level, startDate, endDate) &&
            (level.equals("Municipal") || level.equals("Regional") || level.equals("National"));
    }
}