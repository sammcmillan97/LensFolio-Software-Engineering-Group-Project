package nz.ac.canterbury.seng302.portfolio.authentication;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is a global interceptor for all gRPC clients used by this application. What this means, is that
 * any time a gRPC client sends a request (e.g to the IdentityProvider), the message is 'intercepted' before it
 * actually leaves this application, and we can modify the content of the request here.
 *
 * In this instance, the modification we are making to the intercepted method, is to add in an authentication header
 * using a value that we may or may not have stored in a cookie in the browser. This means that every request we
 * send via a gRPC client will automatically have any authentication session token included, without any need
 * for adding this in ourselves - wonderful!
 */
@GrpcGlobalClientInterceptor
public class AuthenticationClientInterceptor implements ClientInterceptor {

    private final Metadata.Key<String> sessionTokenHeaderKey = Metadata.Key.of("X-Authorization", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * For every call that a client sends, perform the following actions before it is sent:
     *  1.  Attempt to retrieve some information about the HTTP session between this application and the user's browser
     *  2.  Look for a cookie with the name 'lens-session-token' and find its value. If the cookie is not found, the
     *      value will be null
     *  3.  Add a new HTTP header to the gRPC request, of the following format
     *      Header name: "X-Authorization"
     *      Header value: "Bearer {value of cookie, or blank if null}"
     */
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String sessionToken = CookieUtil.getValue(request, "lens-session-token");

        // Every time we send a gRPC request, include a copy of our authentication token in the headers
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(sessionTokenHeaderKey, String.format("Bearer %s", sessionToken));
                super.start(responseListener, headers);
            }
        };
    }
}
