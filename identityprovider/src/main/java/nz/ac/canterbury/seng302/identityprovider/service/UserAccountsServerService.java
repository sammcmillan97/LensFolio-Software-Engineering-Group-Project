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

        if (repository.existsById(request.getUserId())) {
            User user = repository.findByUserId(request.getUserId());
            if (user.checkPassword(request.getCurrentPassword())) {
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

        EditUserResponse.Builder reply = EditUserResponse.newBuilder();

        if (repository.existsById(request.getUserId())) {
            User user = repository.findByUserId(request.getUserId());
            user.setFirstName(request.getFirstName());
            user.setMiddleName(request.getMiddleName());
            user.setLastName(request.getLastName());
            user.setNickname(request.getNickname());
            user.setBio(request.getBio());
            user.setPersonalPronouns(request.getPersonalPronouns());
            user.setEmail(request.getEmail());
            reply.setIsSuccess(true).setMessage("Edit user succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Edit user failed: user does not exist");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }

    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {

        UserResponse.Builder reply = UserResponse.newBuilder();

        if (repository.existsById(request.getId())) {
            User user = repository.findByUserId(request.getId());
            reply.setUsername(user.getUsername())
                    .setFirstName(user.getFirstName())
                    .setMiddleName(user.getMiddleName())
                    .setLastName(user.getLastName())
                    .setNickname(user.getNickname())
                    .setBio(user.getBio())
                    .setPersonalPronouns(user.getPersonalPronouns())
                    .setEmail(user.getEmail())
                    .setCreated(user.getTimeCreated());
        }

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
            reply
                    .setIsSuccess(true)
                    .setNewUserId(repository.findByUsername(request.getUsername()).getUserId())
                    .setMessage("Register attempt succeeded");
        } else {
            reply.setIsSuccess(false).setMessage("Register attempt failed: Username already taken");
        }

        responseObserver.onNext(reply.build());
        responseObserver.onCompleted();

    }

}
