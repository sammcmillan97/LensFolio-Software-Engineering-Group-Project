package nz.ac.canterbury.seng302.identityprovider.authentication;

import io.grpc.*;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;

@GrpcGlobalServerInterceptor
public class AuthenticationServerInterceptor implements ServerInterceptor {

    private final Metadata.Key<String> sessionTokenHeaderKey = Metadata.Key.of("X-Authorization", Metadata.ASCII_STRING_MARSHALLER);

    public static final Context.Key<String> SESSION_TOKEN = Context.key("lens-session-token");
    public static final Context.Key<AuthState> AUTH_STATE = Context.key("auth-state");


    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String sessionToken = headers.get(sessionTokenHeaderKey);
        String bearerStrippedSessionToken = sessionToken != null ? sessionToken.replaceFirst("Bearer ", "") : "";
        AuthState authState = AuthenticationValidatorUtil.validateTokenForAuthState(bearerStrippedSessionToken);

        Context context = Context.current()
                .withValue(SESSION_TOKEN, sessionToken)
                .withValue(AUTH_STATE, authState);

        return Contexts.interceptCall(context, call, headers, next);
    }
}