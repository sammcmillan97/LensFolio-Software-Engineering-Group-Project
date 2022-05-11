package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectEditsController {

    private long editedAtTime;

    @GetMapping("/projects/{id}/editStatus")
    public String projectEditing(@PathVariable("id") String id) {
        if (System.currentTimeMillis() - editedAtTime < 5000) {
            return "1";
        } else {
            return "0";
        }
    }

    @PostMapping("/projects/{id}/editing")
    public void isEditingProject(@PathVariable("id") String id) {
        editedAtTime = System.currentTimeMillis();
    }

}
