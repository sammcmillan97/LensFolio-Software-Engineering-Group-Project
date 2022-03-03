package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserAccountsServerService extends UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {
        System.out.println("You tried to change the password!");

        ChangePasswordResponse.Builder reply = ChangePasswordResponse.newBuilder();

        reply.setIsSuccess(false).setMessage("Not yet implemented");

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        System.out.println("You tried to edit the user!");

        EditUserResponse.Builder reply = EditUserResponse.newBuilder();

        reply.setIsSuccess(false).setMessage("Not yet implemented");

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        System.out.println("You tried to get a user's information by their ID!");

        UserResponse.Builder reply = UserResponse.newBuilder();

        reply.setUsername("Not yet implemented");

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

    @Override
    public void register(UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        System.out.println("You tried to create a user!");

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        reply.setIsSuccess(false).setMessage("Not yet implemented");

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();
    }

}
