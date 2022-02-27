package nz.ac.canterbury.seng302.portfolio.service;

import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticationServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class AuthenticateClientService {

    @GrpcClient("identity-provider-grpc-server")
    private AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authenticationStub;

    public AuthenticateResponse authenticate(final String username, final String password)  {
        AuthenticateRequest authRequest = AuthenticateRequest.newBuilder()
                .setUsername(username)
                .setPassword(password)
                .build();
        return authenticationStub.authenticate(authRequest);
    }

    public AuthState checkAuthState() throws StatusRuntimeException {
        return authenticationStub.checkAuthState(Empty.newBuilder().build());
    }

}
