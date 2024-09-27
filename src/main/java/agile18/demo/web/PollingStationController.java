package agile18.demo.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import agile18.demo.model.PollingStation;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Poll;
import io.micrometer.common.util.StringUtils;

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
            ps.createPoll(UUID.fromString(uuid), body.title(), body.body(), body.level(), body.startDate(), body.endDate());
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
    public List<Poll> onGetUser() {
        return ps.getAllPolls();
    }
}


record BodyOfCreatePoll(String title, String body, String level, String startDate, String endDate) {
    boolean isValid() {
        return !StringUtils.isEmpty(title) && !StringUtils.isEmpty(body) && !StringUtils.isEmpty(level) &&
            !StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate) && 
            (level.equals("Municipal") || level.equals("Regional") || level.equals("National"));
    }
}