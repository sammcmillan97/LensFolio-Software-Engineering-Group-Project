package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.identityprovider.authentication.JwtTokenUtil;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc.AuthenticationServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    @Autowired
    private UserRepository repository;

    private static final String ROLE_OF_USER = "student"; // Puce teams may want to change this to "teacher" to test some functionality

    private final JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    /**
     * Attempts to authenticate a user with a given username and password. 
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        AuthenticateResponse reply = authenticateHandler(request);
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @VisibleForTesting
    AuthenticateResponse authenticateHandler(AuthenticateRequest request) {

        User user = repository.findByUsername(request.getUsername());

        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();

        if (user == null) {
            reply
                    .setMessage("Log in attempt failed: username not registered")
                    .setSuccess(false)
                    .setToken("");
        } else if (Boolean.FALSE.equals(user.checkPassword(request.getPassword()))) {
            reply
                    .setMessage("Log in attempt failed: password incorrect")
                    .setSuccess(false)
                    .setToken("");
        } else {
            String token = jwtTokenService.generateTokenForUser(user.getUsername(), user.getUserId(),
                    user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName(), ROLE_OF_USER);
            reply
                    .setEmail(user.getEmail())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMessage("Logged in successfully!")
                    .setSuccess(true)
                    .setToken(token)
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername());
        }

        return reply.build();
    }

    /**
     * The AuthenticationInterceptor already handles validating the authState for us, so here we just need to
     * retrieve that from the current context and return it in the gRPC body
     */
    @Override
    public void checkAuthState(Empty request, StreamObserver<AuthState> responseObserver) {
        responseObserver.onNext(AuthenticationServerInterceptor.AUTH_STATE.get());
        responseObserver.onCompleted();
    }
}
