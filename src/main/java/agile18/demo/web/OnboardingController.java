package agile18.demo.web;

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
    public String onGetUser(/* @RequestParam String user */) {
        var citizens = db.getAllCitizens();
        String total = "";
        for (User user : citizens) {
            total += "Name: " + user.name() + " & id: " + user.id() + "<br>";
        }
        return total;
    }
}
