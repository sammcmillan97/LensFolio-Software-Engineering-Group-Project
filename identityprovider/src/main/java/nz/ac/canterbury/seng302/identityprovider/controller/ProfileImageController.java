package nz.ac.canterbury.seng302.identityprovider.controller;

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

    @Value("${ENV}")
    private String env;

    @GetMapping("/ProfilePicture-{filename}")
    public ResponseEntity<byte[]> getProfileImage(
            @PathVariable("filename") String filename
    ) {
        try {
            System.out.println("Get PP called");
            File currentDirFile = new File(".");
            String helper = currentDirFile.getAbsolutePath();
            helper = helper.substring(0, helper.length() - 1);
            System.out.println(helper);
            Path photoRelPath = Path.of(helper + "profile-images\\" + env + filename);
            System.out.println(helper + "profile-images\\" + env + filename);
            InputStream inputStream = new FileInputStream(photoRelPath.toFile());
            byte[] bytes = StreamUtils.copyToByteArray(inputStream);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception e) {
            System.out.println("Exception:-" + e);
        }

    }
}
