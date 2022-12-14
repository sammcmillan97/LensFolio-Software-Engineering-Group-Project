package nz.ac.canterbury.seng302.identityprovider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class IdpErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;
    private static final Logger IDENTITY_LOGGER = LoggerFactory.getLogger("com.identity");

    /**
     * Returns a generic error page when an error occurs.
     * @param request A HTTP Request
     * @return a basic error page
     */
    @GetMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> newErrorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, errorAttributeOptions);
        final StringBuilder errorDetails = new StringBuilder();
        newErrorAttributes.forEach((attribute, value) -> errorDetails.append("<tr><td>")
                .append(attribute)
                .append("</td><td><pre>")
                .append(value)
                .append("</pre></td></tr>"));

        String errorMessage = errorDetails.toString();
        IDENTITY_LOGGER.error(errorMessage);

        return String.format("<html><head><style>td{vertical-align:top;border:solid 1px #A82810;}</style>"
                + "</head><body><h2>Identity Provider - Error Page</h2><table>%s</table></body></html>", errorDetails);
    }
}
