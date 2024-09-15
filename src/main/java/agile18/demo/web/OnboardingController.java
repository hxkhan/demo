package agile18.demo.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import agile18.demo.model.Onboarder;
import agile18.demo.model.User;

@RestController
public class OnboardingController {
    private final Onboarder onboarding;

    public OnboardingController(Onboarder ob) {
        this.onboarding = ob;
    }

    @GetMapping("/")
    public String onGetIndex() {
        return "Hello World";
    }

    // Just for funsies
    @GetMapping("/users")
    public List<User> onGetUser() {
        var citizens = onboarding.getAllCitizens();
        return citizens;
    }
}
