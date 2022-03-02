package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Long registerUser(User user) {
        //validation and functionality required
        userRepository.save(user);
        return user.getUserId();
    }

    public Long login(User user) {
        //validation and functionality required
        return user.getUserId();
    }

}
