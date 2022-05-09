package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@Configuration
public class IdentityProviderApplication implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository repository) {
        return args -> {
            // save a few users
            repository.save(new User("bauerjac","Jack", "Brown", "Bauer","Jack-Jack", "howdy", "he/him", "jack@gmail.com", "password"));
            repository.save(new User("obrianchl","Chloe", "Pearl", "OBrian", "Coco", "hello", "she/her", "coco@gmail.com", "password"));
            repository.save(new User("bauerkim","Kim", "Dally", "Bauer", "Kiki", "heyy", "she/her", "kiki@gmail.com", "password"));
            repository.save(new User("dr big","Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "Blueblueblueblueblueblueblueblueblueblueblueblueblueblueblueblue", "Palmerluebluebluebluebluebluelueblueblueblueblueblueblueblueblue", "Davidavidavidavidavidavidavidavidavidavidavidavidavidavidavidavy", "According to all known laws of aviation, there is no way a bee should be able to fly. Its wings are too small to get its fat little body off the ground. The bee, of course, flies anyway because bees don't care what humans think is impossible. Yellow, black. Yellow, black. Yellow, black. Yellow, black. Ooh, black and yellow! Let's shake it up a little. Barry! Breakfast is ready! Ooming! Hang on a second. Hello? - Barry? - Adam? - Oan you believe this is happening? - I can't. I'll pick you up. Looking sharp. Use the stairs. Your father paid good money for those. Sorry. I'm excited. Here's the graduate. We're very proud of you, son. A perfect report card, all B's. Very proud. Ma! I got a thing going here. - You got lint on your fuzz. - Ow! That's me! - Wave to us! We'll be in row 118,000. - Bye! Barry, I told you, stop flying in the house! - Hey, Adam. - Hey, Barry. - Is that fuzz gel? - A little. Special day, graduation. Never thought I'd make it. Three days grade school, three days high school. Those were awkw", "long/longggggggggggggggggggggggggggggggggggggggggggggggggggglong", "long@gmail.comomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomomommomomomomomomomom", "password"));
            repository.save(new User("dr small","M", "", "D", "", "", "", "s@s.s", "password"));
            repository.save(new User("abc123","Michelle", "Harriet", "Dessler", "Shelly", "hi", "he/him", "shelly@gmail.com", "Password123!"));
            User teacher = new User("teacher", "Tee", "A", "Cher", "", "I am the teacher of this class", "she/her", "teacher@uc.ac.nz", "password");
            teacher.addRole(UserRole.TEACHER);
            repository.save(teacher);
            User admin = new User("admin", "Ann", "", "Dim", "Master", "King of the world", "she/her", "admin@uc.ac.nz", "password");
            admin.addRole(UserRole.TEACHER);
            admin.addRole(UserRole.COURSE_ADMINISTRATOR);
            repository.save(admin);
            User onlyAdmin = new User("og", "True", "Pure", "Admin", "H8xor", "I don't need to be king", "", "onlyadmin@uc.ac.nz", "password");
            onlyAdmin.addRole(UserRole.COURSE_ADMINISTRATOR);
            onlyAdmin.removeRole(UserRole.STUDENT);
            repository.save(onlyAdmin);
            for (int i = 0; i < 100; i++) {
                repository.save(new User("test" + i,"Test", "", "Clone","Tester", "", "", "tester@gmail.com", "password"));
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("file:src/main/resources/");
    }

}
