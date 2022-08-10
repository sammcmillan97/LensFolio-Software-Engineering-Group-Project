package nz.ac.canterbury.seng302.portfolio;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class WebConfig implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    private static final String ERROR = "/errors";
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        factory.addErrorPages(
                new ErrorPage(HttpStatus.UNAUTHORIZED, ERROR),
                new ErrorPage(HttpStatus.FORBIDDEN, ERROR),
                new ErrorPage(HttpStatus.NOT_FOUND, ERROR),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, ERROR));
    }
}
