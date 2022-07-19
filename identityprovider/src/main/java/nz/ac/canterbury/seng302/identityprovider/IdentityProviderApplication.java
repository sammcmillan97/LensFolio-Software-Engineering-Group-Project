package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.Group;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.GroupRepository;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Configuration
public class IdentityProviderApplication implements WebMvcConfigurer {

    @Value("${IMAGE_SRC}")
    private String imageSrc;

    @Value("${ENV}")
    private String env;

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_PRONOUNS = "she/her";

    @Bean
    public CommandLineRunner demo(UserRepository userRepository, GroupRepository groupRepository) {
        return args -> {
            if (userRepository.count() != 0)
                return;
            // save a few users
            userRepository.save(new User("bauerjac","Jack", "Brown", "Bauer","Jack-Jack", "howdy", DEFAULT_PRONOUNS, "jack@gmail.com", DEFAULT_PASSWORD));
            userRepository.save(new User("obrianchl","Chloe", "Pearl", "OBrian", "Coco", "hello", DEFAULT_PRONOUNS, "coco@gmail.com", DEFAULT_PASSWORD));
            userRepository.save(new User("bauerkim","Kim", "Dally", "Bauer", "Kiki", "heyy", DEFAULT_PRONOUNS, "kiki@gmail.com", DEFAULT_PASSWORD));
            userRepository.save(new User("dr big","Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "Blueblueblueblueblueblueblueblueblueblueblueblueblueblueblueblue", "Palmerluebluebluebluebluebluelueblueblueblueblueblueblueblueblue", "Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "According to all known laws of aviation, there is no way a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyway because bees don't care what humans think is impossible. Yellow, black. Yellow, black. Yellow, black. Yellow, black. Ooh, black and yellow! Let's shake it up a little. Barry! Breakfast is ready! Ooming! Hang on a second. Hello? - Barry? - Adam? - Oan you believe this is happening? - I can't. I'll pick you up. Looking sharp. Use the stairs. Your father paid good money for those. Sorry. I'm excited. Here's the graduate. We're very proud of you, son. A perfect report card, all B's. Very proud. Ma! I got a thing going here. - You got lint on your fuzz. - Ow! That's me! - Wave to us! We'll be in row 118,000. - Bye! Barry, I told you, stop flying in the house! - Hey, Adam. - Hey, Barry. - Is that fuzz gel? - A little. Special day, graduation. Never thought I'd make it. Three days grade school, three days high school. Those were awkw", "long/longggggggggggggggggggggggggggggggggggggggggggggggggggglong", "long@gmail.comomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomommomomomomomomomom", DEFAULT_PASSWORD));
            userRepository.save(new User("dr small","M", "", "D", "", "", "", "s@s.s", DEFAULT_PASSWORD));
            userRepository.save(new User("abc123","Michelle", "Harriet", "Dessler", "Shelly", "hi", DEFAULT_PRONOUNS, "shelly@gmail.com", "Password123!"));
            User teacher = new User("teacher", "Tee", "A", "Cher", "", "I am the teacher of this class", DEFAULT_PRONOUNS, "teacher@uc.ac.nz", DEFAULT_PASSWORD);
            teacher.addRole(UserRole.TEACHER);
            userRepository.save(teacher);
            User admin = new User("admin", "Ann", "", "Dim", "Master", "King of the world", DEFAULT_PRONOUNS, "admin@uc.ac.nz", DEFAULT_PASSWORD);
            admin.addRole(UserRole.TEACHER);
            admin.addRole(UserRole.COURSE_ADMINISTRATOR);
            userRepository.save(admin);
            User onlyAdmin = new User("og", "True", "Pure", "Admin", "H8xor", "I don't need to be king", "", "onlyadmin@uc.ac.nz", DEFAULT_PASSWORD);
            onlyAdmin.addRole(UserRole.COURSE_ADMINISTRATOR);
            onlyAdmin.removeRole(UserRole.STUDENT);
            userRepository.save(onlyAdmin);

            User user1 = new User("testUser1", "Frank", "Frankie", "McFrank", "Frankie", "I am Frank", "he/him", "frank@frank.com", "frank123");
            User user2 = new User("testUser2", "Frank2", "Frankie2", "McFrank2", "Frankie2", "I am Frank2", "he/him", "frank2@frank.com", "frank123");
            User user3 = new User("testUser3", "Frank3", "Frankie3", "McFrank3", "Frankie3", "I am Frank3", "he/him", "frank3@frank.com", "frank123");
            Group group1 = new Group("Group 1", "cTest group 1 long name", 1);
            Group group2 = new Group("Group 2", "aTest group 2 long name", 1);
            Group group3 = new Group("Group 3", "bTest group 3 long name", 1);
            group1.addMember(user1);
            group1.addMember(user2);
            group1.addMember(user3);
            group2.addMember(user1);
            group2.addMember(user2);
            group3.addMember(user1);
            groupRepository.save(group1);
            groupRepository.save(group2);
            groupRepository.save(group3);

        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/profile-images/" + env  + "**")
                .addResourceLocations("file:" + imageSrc + env);
    }

}
