package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.service.UserAccountsServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Controller
public class ProfileImageController {

    @Autowired
    private UserAccountsServerService userAccountsServerService;

    @GetMapping("/ProfilePicture-{filename}")
    public ResponseEntity<byte[]> ProfilePicture(
            @PathVariable("filename") String filename
    ) {
        System.out.println("Get PP in controller called");
        try {
            byte[] bytes = userAccountsServerService.getProfilePicture(filename);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception e) {
            System.out.println("Exception:-" + e);
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }
    }
}
