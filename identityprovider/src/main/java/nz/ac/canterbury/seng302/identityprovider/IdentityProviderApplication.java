package nz.ac.canterbury.seng302.identityprovider;

import nz.ac.canterbury.seng302.identityprovider.entity.Client;
import nz.ac.canterbury.seng302.identityprovider.repository.ClientRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class IdentityProviderApplication {

    private static final Logger log = LoggerFactory.getLogger(IdentityProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IdentityProviderApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ClientRepository repository) {
        return (args) -> {
            // save a few users
            repository.save(new Client("Jack", "Bauer"));
            repository.save(new Client("Chloe", "O'Brian"));
            repository.save(new Client("Kim", "Bauer"));
            repository.save(new Client("David", "Palmer"));
            repository.save(new Client("Michelle", "Dessler"));

            // fetch all users
            log.info("Users found with findAll():");
            log.info("-------------------------------");
            for (Client user : repository.findAll()) {
                log.info(user.toString());
            }
            log.info("");

            // fetch an individual customer by ID
            Client user = repository.findByUserId(1L);
            log.info("user found with findById(1L):");
            log.info("--------------------------------");
            log.info(user.toString());
            log.info("");

            // fetch customers by last name
            log.info("User found with findByUsername('Jack'):");
            log.info("--------------------------------------------");
            repository.findByUsername("Jack").toString();

            log.info("");
        };
    }
}
