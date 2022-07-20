package nz.ac.canterbury.seng302.identityprovider;

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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/profile-images/" + env  + "**")
                .addResourceLocations("file:" + imageSrc + env);
    }

}
