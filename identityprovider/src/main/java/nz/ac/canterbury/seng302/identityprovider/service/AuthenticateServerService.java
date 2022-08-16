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
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@GrpcService
public class AuthenticateServerService extends AuthenticationServiceImplBase{

    @Autowired
    private UserRepository repository;

    private final JwtTokenUtil jwtTokenService = JwtTokenUtil.getInstance();

    @Autowired
    private UserAccountsServerService userAccountsServerService;
    private static final Logger IDENTITY_LOGGER = LoggerFactory.getLogger("com.identity");

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
        AuthenticateResponse.Builder reply = AuthenticateResponse.newBuilder();
        if (userAccountsServerService.isBadUserName(request.getUsername())){
            IDENTITY_LOGGER.info("Log in attempt failed: cannot contain special characters");
            reply
                    .setMessage("Log in attempt failed: username cannot contain special characters")
                    .setSuccess(false)
                    .setToken("");
            return reply.build();
        }

        User user = repository.findByUsername(request.getUsername());
        if (user == null) {
            IDENTITY_LOGGER.info("Log in attempt failed: username not registered");
            reply
                    .setMessage("Log in attempt failed: username not registered")
                    .setSuccess(false)
                    .setToken("");
        } else if (Boolean.FALSE.equals(user.checkPassword(request.getPassword()))) {
            IDENTITY_LOGGER.info("Log in attempt failed: password incorrect");
            reply
                    .setMessage("Log in attempt failed: password incorrect")
                    .setSuccess(false)
                    .setToken("");
        } else {
            ArrayList<String> usersRoles = new ArrayList<>();
            for (UserRole role: user.getRoles()) {
                if (role == UserRole.STUDENT) {
                    usersRoles.add("student");
                }
                if (role == UserRole.TEACHER) {
                    usersRoles.add("teacher");
                }
                if (role == UserRole.COURSE_ADMINISTRATOR) {
                    usersRoles.add("courseadministrator");
                }
            }

            String token = jwtTokenService.generateTokenForUser(user.getUsername(), user.getUserId(),
                    user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName(), String.join(",", usersRoles));
            reply
                    .setEmail(user.getEmail())
                    .setFirstName(user.getFirstName())
                    .setLastName(user.getLastName())
                    .setMessage("Logged in successfully!")
                    .setSuccess(true)
                    .setToken(token)
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername());
            String loggerMessage = String.format("User #%d: %s logged in successfully", user.getUserId(), user.getUsername());
            IDENTITY_LOGGER.info(loggerMessage);
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
