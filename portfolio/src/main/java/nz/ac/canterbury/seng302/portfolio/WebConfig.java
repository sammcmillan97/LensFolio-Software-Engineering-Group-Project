package nz.ac.canterbury.seng302.portfolio;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class WebConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.addErrorPages(
                new ErrorPage(HttpStatus.UNAUTHORIZED, "/errors"),
                new ErrorPage(HttpStatus.FORBIDDEN, "/errors"),
                new ErrorPage(HttpStatus.NOT_FOUND, "/errors"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/errors"));
    }
}
