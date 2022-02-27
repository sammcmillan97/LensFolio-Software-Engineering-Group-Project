package nz.ac.canterbury.seng302.portfolio.service;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.shared.identityprovider.HelloResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.HelloRequest;

import org.springframework.stereotype.Service;

import nz.ac.canterbury.seng302.shared.identityprovider.GreeterGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class GreeterClientService {

    @GrpcClient(value = "identity-provider-grpc-server")
    private GreeterGrpc.GreeterBlockingStub greeterStub;

    public String receiveGreeting(final String favouriteColour) throws StatusRuntimeException {
        HelloResponse response = greeterStub.sayHello(HelloRequest.newBuilder().setFavouriteColour(favouriteColour).build());
        return response.getMessage();
    }
}
