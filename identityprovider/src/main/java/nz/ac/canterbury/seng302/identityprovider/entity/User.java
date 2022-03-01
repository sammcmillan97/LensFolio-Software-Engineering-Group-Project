package nz.ac.canterbury.seng302.identityprovider.entity;

import nz.ac.canterbury.seng302.identityprovider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {

    @Autowired
    private UserRepository repository;

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;

    private String username;

    private String password;

    protected User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format (
                "User[userId=%d, username=%s]",
                userId, username);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }
}
