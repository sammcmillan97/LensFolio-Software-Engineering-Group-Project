package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectEditsController {

    private boolean test = true;

    @GetMapping("/projects/{id}/editStatus")
    public String projectEditing(@PathVariable("id") String id) {
        test = !test;
        return test ? ">.<" : "<.>";
    }

}
