package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.entity.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository  extends CrudRepository <User, Integer> {
    User findByUsername(String username);

    User findByUserId(Integer userId);
}
