package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.authentication.AuthenticationServerInterceptor;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@GrpcService
public class GreeterServerService extends GreeterGrpc.GreeterImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GreeterServerService.class);

    private static final String[] helloMessages = {
      "Guten Tag",
      "Hello",
      "Bonjour",
      "Hola"
    };

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        logger.info("sayHello() has been called");

        // Pull the authState out of the current context
        AuthState authState = AuthenticationServerInterceptor.AUTH_STATE.get();

        String helloMessage = helloMessages[(new Random()).nextInt(helloMessages.length)];
        String role = authState.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        HelloResponse reply = HelloResponse.newBuilder().setMessage(
                String.format(
                        "%s! The IdP can see you are logged in as '%s' (role='%s'), and you told me your favourite colour is %s.",
                        helloMessage,
                        authState.getName(),
                        role,
                        request.getFavouriteColour()
                    )
            ).build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
