package agile18.demo.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OnboardingController {
    @GetMapping("/")
    public String onGetIndex() {
        return "Hello World";
    }
}
