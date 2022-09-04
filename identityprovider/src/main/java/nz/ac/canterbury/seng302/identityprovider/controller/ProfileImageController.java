package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.service.UserAccountsServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class ProfileImageController {

    @Autowired
    private UserAccountsServerService userAccountsServerService;
    private static final Logger IDENTITY_LOGGER = LoggerFactory.getLogger("com.identity");

    /**
     * Finds the request profile picture if it exists and returns a byte array
     * @param filename the filename of the profile picture to retrieve
     * @return a byte array containing the image data
     */
    @GetMapping("/ProfilePicture-{filename}")
    public ResponseEntity<byte[]> getProfilePicture(
            @PathVariable("filename") String filename
    ) {
        try {
            byte[] bytes = userAccountsServerService.getProfilePicture(filename);
            IDENTITY_LOGGER.info("Profile picture found.");
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception e) {
            IDENTITY_LOGGER.info("Profile picture file not found.");
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }
    }
}
