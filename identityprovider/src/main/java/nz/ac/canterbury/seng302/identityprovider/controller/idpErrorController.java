package nz.ac.canterbury.seng302.identityprovider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class idpErrorController implements ErrorController {

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        ServletWebRequest servletWebRequest = new ServletWebRequest(request);
        ErrorAttributeOptions errorAttributeOptions = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(servletWebRequest, errorAttributeOptions);
        final StringBuilder errorDetails = new StringBuilder();
        errorAttributes.forEach((attribute, value) -> {
            errorDetails.append("<tr><td>")
                    .append(attribute)
                    .append("</td><td><pre>")
                    .append(value)
                    .append("</pre></td></tr>");
        });

        return String.format("<html><head><style>td{vertical-align:top;border:solid 1px #A82810;}</style>"
                + "</head><body><h2>Identity Provider - Error Page</h2><table>%s</table></body></html>", errorDetails.toString());
    }
}
