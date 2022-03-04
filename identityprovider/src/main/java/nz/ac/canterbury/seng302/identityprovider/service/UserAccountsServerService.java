package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc.UserAccountServiceImplBase;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@GrpcService
public class UserAccountsServerService extends UserAccountServiceImplBase {

    @Autowired
    private UserRepository repository;

    @Override
    public void changeUserPassword(ChangePasswordRequest request, StreamObserver<ChangePasswordResponse> responseObserver) {

        ChangePasswordResponse.Builder reply = ChangePasswordResponse.newBuilder();

        if (repository.existsById((long) request.getUserId())) {
            User user = repository.findByUserId((long) request.getUserId());
            if (Objects.equals(user.getPassword(), request.getCurrentPassword())) {
                user.setPassword(request.getNewPassword());
                reply.setIsSuccess(true).setMessage("Successfully changed password");
            } else {
                reply.setIsSuccess(false).setMessage("Password change failed: current password is incorrect");
            }
        } else {
            reply.setIsSuccess(false).setMessage("Password change failed: user does not exist");
        }

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

        UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

        if (repository.findByUsername(request.getUsername()) == null) { //Middle name
            repository.save(new User(
                    request.getUsername(),
                    request.getFirstName(),
                    request.getMiddleName(),
                    request.getLastName(),
                    request.getNickname(),
                    request.getBio(),
                    request.getPersonalPronouns(),
                    request.getEmail(),
                    request.getPassword()));
            reply.setIsSuccess(true).setMessage("Register attempt succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Register attempt failed: Username already taken");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }

}
