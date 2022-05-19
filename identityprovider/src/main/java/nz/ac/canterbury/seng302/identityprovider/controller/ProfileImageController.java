package nz.ac.canterbury.seng302.identityprovider.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Controller
public class ProfileImageController {

    @GetMapping("/getProfilePicture")
    public ResponseEntity<byte[]> getProfileImage() throws IOException {

        String filename = "default.jpg";
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath();
        helper = helper.substring(0, helper.length() - 1);
        Path photoRelPath = Path.of(helper + "src\\main\\resources\\profile-images\\default\\" + filename);
        InputStream inputStream = new FileInputStream(photoRelPath.toFile());
        byte[] bytes = StreamUtils.copyToByteArray(inputStream);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }



}
