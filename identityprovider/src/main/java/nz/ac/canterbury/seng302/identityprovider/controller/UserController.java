package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/some/api/path")
    @ResponseBody
    public Long register(@RequestBody User user) {
        //validation and functionality required
        return userService.registerUser(user);
    }

    @RequestMapping("/some/api/path")
    @ResponseBody
    public Long login(@RequestBody User user) {
        //validation and functionality required
        return userService.login(user);
    }
}
