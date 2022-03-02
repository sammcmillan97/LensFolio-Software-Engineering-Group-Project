package nz.ac.canterbury.seng302.identityprovider.repository;

import nz.ac.canterbury.seng302.identityprovider.entity.Client;
import org.springframework.data.repository.CrudRepository;


public interface ClientRepository  extends CrudRepository <Client, Long> {
    Client findByUsername(String username);

    Client findByUserId(Long userId);
}
