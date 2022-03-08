package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;

@SpringBootApplication
public class IdentityProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(UserRepository repository) {
        return (args) -> {
            // save a few users
            repository.save(new User("bauerjac","Jack", "Brown", "Bauer","Jack-Jack", "howdy", "he/him", "jack@gmail.com", "password"));
            repository.save(new User("obrianchl","Chloe", "Pearl", "OBrian", "Coco", "hello", "she/her", "coco@gmail.com", "password"));
            repository.save(new User("bauerkim","Kim", "Dally", "Bauer", "Kiki", "heyy", "she/her", "kiki@gmail.com", "password"));
            repository.save(new User("palmerdav","David", "Blue", "Palmer", "Davo", "gidday", "they/them", "davo@gmail.com", "password"));
            repository.save(new User("desslermic","Michelle", "Harriet", "Dessler", "Shelly", "hi", "he/him", "shelly@gmail.com", "password"));
        };
    }
}
