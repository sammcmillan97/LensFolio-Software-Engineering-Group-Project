package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class DevTools {

    @Autowired
    UserRepository userRepository;

    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_PRONOUNS = "she/her";
    private static final String ADMIN_PASSWORD = "password400";


    @RequestMapping("/addExampleUsers")
    protected void addExampleUsers(){
        userRepository.save(new User("bauerjac","Jack", "Brown", "Bauer","Jack-Jack", "howdy", "he/him", "jack@gmail.com", DEFAULT_PASSWORD));
        userRepository.save(new User("obrianchl","Chloe", "Pearl", "OBrian", "Coco", "hello", DEFAULT_PRONOUNS, "coco@gmail.com", DEFAULT_PASSWORD));
        userRepository.save(new User("bauerkim","Kim", "Dally", "Bauer", "Kiki", "heyy", DEFAULT_PRONOUNS, "kiki@gmail.com", DEFAULT_PASSWORD));
        userRepository.save(new User("dr big","Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "Blueblueblueblueblueblueblueblueblueblueblueblueblueblueblueblue", "Palmerluebluebluebluebluebluelueblueblueblueblueblueblueblueblue", "Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "According to all known laws of aviation, there is no way a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyway because bees don't care what humans think is impossible. Yellow, black. Yellow, black. Yellow, black. Yellow, black. Ooh, black and yellow! Let's shake it up a little. Barry! Breakfast is ready! Ooming! Hang on a second. Hello? - Barry? - Adam? - Oan you believe this is happening? - I can't. I'll pick you up. Looking sharp. Use the stairs. Your father paid good money for those. Sorry. I'm excited. Here's the graduate. We're very proud of you, son. A perfect report card, all B's. Very proud. Ma! I got a thing going here. - You got lint on your fuzz. - Ow! That's me! - Wave to us! We'll be in row 118,000. - Bye! Barry, I told you, stop flying in the house! - Hey, Adam. - Hey, Barry. - Is that fuzz gel? - A little. Special day, graduation. Never thought I'd make it. Three days grade school, three days high school. Those were awkw", "long/longggggggggggggggggggggggggggggggggggggggggggggggggggglong", "long@gmail.comomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomommomomomomomomomom", DEFAULT_PASSWORD));
        userRepository.save(new User("dr small","M", "", "D", "", "", "", "s@s.s", DEFAULT_PASSWORD));
        userRepository.save(new User("abc123","Michelle", "Harriet", "Dessler", "Shelly", "hi", "he/him", "shelly@gmail.com", "Password123!"));
        User teacher = new User("teacher", "Tee", "A", "Cher", "", "I am the teacher of this class", DEFAULT_PRONOUNS, "teacher@uc.ac.nz", ADMIN_PASSWORD);
        teacher.addRole(UserRole.TEACHER);
        userRepository.save(teacher);
        User admin = new User("admin", "Ann", "", "Dim", "Master", "King of the world", DEFAULT_PRONOUNS, "admin@uc.ac.nz", ADMIN_PASSWORD);
        admin.addRole(UserRole.TEACHER);
        admin.addRole(UserRole.COURSE_ADMINISTRATOR);
        userRepository.save(admin);
        User onlyAdmin = new User("og", "True", "Pure", "Admin", "H8xor", "I don't need to be king", "", "onlyadmin@uc.ac.nz", ADMIN_PASSWORD);
        onlyAdmin.addRole(UserRole.COURSE_ADMINISTRATOR);
        onlyAdmin.removeRole(UserRole.STUDENT);
        userRepository.save(onlyAdmin);
    }

    @RequestMapping("/deleteExampleUsers")
    protected void deleteExampleUsers(){
        User testUser = null;

        ArrayList<String> usernames = new ArrayList<>(
                Arrays.asList("bauerjac", "obrianchl", "bauerkim", "dr big", "dr small", "abc123", "teacher", "admin", "og"));
        Iterator<String> usernameIterator = usernames.iterator();
        while(usernameIterator.hasNext()) {
            testUser = userRepository.findByUsername(usernameIterator.next());
            userRepository.deleteById(testUser.getUserId());
        }
    }

}
