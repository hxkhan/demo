package agile18.demo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import agile18.Utils;
import agile18.demo.model.LevelEnum;
import agile18.demo.model.Onboarder;
import agile18.demo.model.PollBrowser;
import agile18.demo.model.PollStatusEnum;
import agile18.demo.model.PollingStation;
import agile18.demo.model.VoteEnum;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.demo.model.Records.Poll;

@RestController
public class PollingController {
    private final PollingStation ps;
    private final Onboarder ob;
    private final PollBrowser pb;

    public PollingController(PollingStation ps, Onboarder ob, PollBrowser pb) {
        this.ps = ps;
        this.ob = ob;
        this.pb = pb;
    }

    @PostMapping("/create-poll")
    public Map<String, Object> onCreatePoll(@RequestParam String uuid, @RequestBody BodyOfCreatePoll body) {
        if (!body.isValid()) {
            return Map.of(
                    "success", false,
                    "message", "invalid request body");
        }

        try {
            ps.createPoll(UUID.fromString(uuid), body.title(), body.body(), body.level(), body.startDate(),
                    body.endDate());
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

    @PostMapping("/cast-vote")
    public Map<String, Object> onCastVote(@RequestParam String uuid, @RequestBody BodyOfCastVote body) {
        if (!body.isValid()) {
            return Map.of(
                    "success", false,
                    "message", "invalid request body");
        }

        try {
            ps.castVote(UUID.fromString(uuid), body.id(), body.vote());
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
        } catch (PollDoesNotExistException e) {
            return Map.of(
                    "success", false,
                    "message", "poll does not exist");
        } catch (CitizenHasAlreadyCastedException e) {
            return Map.of(
                    "success", false,
                    "message", "already casted");
        } catch (UnAuthorisedToVote e) {
            return Map.of(
                    "success", false,
                    "message", "unauthorised to vote");
        }
    }

    @GetMapping("/polls")
    public List<Poll> onGetPolls() {
        return ps.getAllPolls();
    }

    @GetMapping("/municipal-polls")
    public Map<String, Object> onGetMunicipalPolls(@RequestParam String uuid, @RequestParam PollStatusEnum status) {
        try {
            Citizen c = ob.checkLogin(UUID.fromString(uuid));
            List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
            return Map.of(
                    "success", true,
                    "polls", pb.getMunPolls(c.home(), status),
                    "casted", casted);
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

    @GetMapping("/regional-polls")
    public Map<String, Object> onGetRegionalPolls(@RequestParam String uuid, @RequestParam PollStatusEnum status) {
        try {
            Citizen c = ob.checkLogin(UUID.fromString(uuid));
            List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
            return Map.of(
                    "success", true,
                    "polls", pb.getRegPolls(c.home(), status),
                    "casted", casted);
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

    @GetMapping("/national-polls")
    public Map<String, Object> onGetNationalPolls(@RequestParam String uuid, @RequestParam PollStatusEnum status) {
        try {
            ob.checkLogin(UUID.fromString(uuid)); // la till för att kolla UUID
            List<Integer> casted = ps.getAllCastsFor(UUID.fromString(uuid));
            return Map.of(
                    "success", true,
                    "polls", pb.getNatPolls(status),
                    "casted", casted);
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

    @GetMapping("/poll")
    public Map<String, Object> onGetPoll(@RequestParam int id) {
        try {
            return Map.of(
                    "success", true,
                    "data", ps.getPollWithID(id));
        } catch (PollDoesNotExistException e) {
            return Map.of(
                    "success", false,
                    "message", "poll does not exist");
        }
    }
}

record BodyOfCastVote(int id, VoteEnum vote) {
    boolean isValid() {
        return vote != null;
    }
}

record BodyOfCreatePoll(String title, String body, LevelEnum level, String startDate, String endDate) {
    boolean isValid() {
        if (Utils.isEmpty(title, body, startDate, endDate) || level == null)
            return false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        try {
            boolean startsBeforeItEnds = sdf.parse(startDate).before(sdf.parse(endDate));
            boolean startsAndEndsOnTheSameDay = startDate.equals(endDate);
            boolean startsAtleastTomorrow = today.before(sdf.parse(startDate));

            if (!(startsAtleastTomorrow && (startsBeforeItEnds || startsAndEndsOnTheSameDay)))
                return false;
        } catch (ParseException e) {
            // invalid dates
            return false;
        }

        return true;
    }
}