package nz.ac.canterbury.seng302.identityprovider.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DevToolsTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DevTools devTools;

    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_PRONOUNS = "she/her";
    private static final String ADMIN_PASSWORD = "password400";

    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
    }

    @Test
    void givenNoUsersExist_loadRepositoryWithExampleUsers(){
        assertEquals(0, userRepository.findAll().size());
        String res = devTools.addExampleUsers();
        assertEquals("<html><head><style>td{vertical-align:top;border:solid 1px #A82810;}</style>"
                + "</head><body><h2>Identity Provider - Users Added</h2></body></html>", res);
        assertEquals(9, userRepository.findAll().size());
    }

    @Test
    void givenUsersExist_loadRepositoryWithExampleUsers(){
        assertEquals(0, userRepository.findAll().size());
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
        Exception exception = assertThrows(Exception.class, () ->
                devTools.addExampleUsers());
        assertEquals("Unable to add users", exception.getMessage());
    }

    @Test
    void givenUsersExist_RemoveExampleUsers(){
        assertEquals(0, userRepository.findAll().size());
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

        String res = devTools.deleteExampleUsers();
        assertEquals("<html><head><style>td{vertical-align:top;border:solid 1px #A82810;}</style>"
                + "</head><body><h2>Identity Provider - Users Deleted</h2></body></html>", res);
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void givenUsersDoNotExist_RemoveExampleUsers(){
        assertEquals(0, userRepository.findAll().size());
        Exception exception = assertThrows(Exception.class, () ->
                devTools.deleteExampleUsers());
        assertEquals("Unable to remove users", exception.getMessage());
    }
}