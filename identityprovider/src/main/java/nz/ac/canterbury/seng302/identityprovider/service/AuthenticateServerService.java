package nz.ac.canterbury.seng302.identityprovider.service;

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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    @Autowired
    private UserRepository repository;

    private final int VALID_USER_ID = 1;
    private final String VALID_USER = "abc123";
    private final String VALID_PASSWORD = "Password123!";
    private final String FIRST_NAME_OF_USER = "Valid";
    private final String LAST_NAME_OF_USER = "User";
    private final String FULL_NAME_OF_USER = FIRST_NAME_OF_USER + " " + LAST_NAME_OF_USER;
    private final String ROLE_OF_USER = "student"; // Puce teams may want to change this to "teacher" to test some functionality

    private JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    /**
     * Attempts to authenticate a user with a given username and password. 
     */
    @Override
    public void authenticate(AuthenticateRequest request, StreamObserver<AuthenticateResponse> responseObserver) {
        User user = repository.findByUsername(request.getUsername());

        System.out.println(user);

        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();
        
        if (user != null && request.getPassword().equals(user.getPassword())) {

            String token = jwtTokenService.generateTokenForUser(user.getUsername(), VALID_USER_ID, FULL_NAME_OF_USER, ROLE_OF_USER);
            reply
                .setEmail("validuser@email.com")
                .setFirstName("VALID")
                .setLastName("USER")
                .setMessage("Logged in successfully!")
                .setSuccess(true)
                .setToken(token)
                .setUserId(1)
                .setUsername(user.getUsername());
        } else {
            reply
            .setMessage("Log in attempt failed: username or password incorrect")
            .setSuccess(false)
            .setToken("");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
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
