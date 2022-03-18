package nz.ac.canterbury.seng302.portfolio.controller;
import org.springframework.web.bind.annotation.GetMapping;


public class testController {
    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
