package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/some/api/path")
    @ResponseStatus(HttpStatus.CREATED)
    public Long register(@RequestBody User user) {
        //validation and functionality required
        return userService.registerUser(user);
    }

    @PostMapping ("/some/api/path2")
    @ResponseStatus(HttpStatus.OK)
    public Long login(@RequestBody User user) {
        //validation and functionality required
        return userService.login(user);
    }
}
