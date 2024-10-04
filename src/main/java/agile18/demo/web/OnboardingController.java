package agile18.demo.web;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import agile18.demo.model.Onboarder;
import agile18.demo.model.Exceptions.*;
import agile18.demo.model.Records.Citizen;
import agile18.Utils;

// Controller for ob processes of users, both new and for login of already existing users
@RestController
public class OnboardingController {
    private final Onboarder ob;

    public OnboardingController(Onboarder ob) {
        this.ob = ob;
    }

    @PostMapping("/login")
    public Map<String, Object> onLoginUser(@RequestBody BodyOfLoginUser body) {
        if (!body.isValid()) {
            return Map.of(
                "success", false,
                "message", "invalid request body"
            );
        }

        try {
            UUID uuid = ob.login(body.id(), body.password());
            return Map.of(
                "success", true,
                "uuid", uuid.toString()
            );
        } catch (CitizenDoesNotExistException e) {
            return Map.of(
                "success", false,
                "message", "user does not exist"
            );
        } catch (IncorrectPasswordException e) {
            return Map.of(
                "success", false,
                "message", "incorrect password"
            );
        }
    }

    //Used for sent form data for register to backend
    @PostMapping("/register")
    public Map<String, Object> onRegisterUser(@RequestBody BodyOfRegisterUser body) {
        if (!body.isValid()) {
            return Map.of(
                "success", false,
                "message", "invalid request body"
            );
        }

        try {
            UUID uuid = ob.register(body.firstName(), body.lastName(), body.id(), body.password(), body.municipality());
            return Map.of(
                "success", true,
                "uuid", uuid.toString()
            );
        } catch (CitizenExistsException e) {
            return Map.of(
                "success", false,
                "message", "user already exists"
            );
        } catch (MunicipalityDoesNotExist e) {
            return Map.of(
                "success", false,
                "message", "invalid municipality"
            );
        }
    }

    @GetMapping("/check-login-status")
    public Map<String, Object> onGetLoginStatus(@RequestParam String uuid) {
        try {
            Citizen c = ob.checkLogin(UUID.fromString(uuid));
            return Map.of(
                    "success", true,
                    "data", c);
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

    // exists for debugging purposes
    @GetMapping("/citizens")
    public List<Citizen> onGetUser() {
        return ob.getAllCitizens();
    }
}

record BodyOfRegisterUser(String firstName, String lastName, String id, String password, String municipality) {
    boolean isValid() {
        return !Utils.isEmpty(firstName, lastName, id, password, municipality) && id.length() == 10;
    }
}

record BodyOfLoginUser(String id, String password) {
    boolean isValid() {
        return !Utils.isEmpty(id, password) && id.length() == 10;
    }
}