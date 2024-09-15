package agile18.demo.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import agile18.demo.model.Database;
import agile18.demo.model.User;

@RestController
public class OnboardingController {
    private final Database db;

    public OnboardingController(Database db) {
        this.db = db;
    }

    @GetMapping("/")
    public String onGetIndex() {
        return "Hello World";
    }

    // Just for funsies
    @GetMapping("/users")
    public List<User> onGetUser(/* @RequestParam String user */) {
        var citizens = db.getAllCitizens();

        return citizens;
    }
}
