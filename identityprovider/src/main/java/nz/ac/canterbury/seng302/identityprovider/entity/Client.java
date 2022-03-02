package nz.ac.canterbury.seng302.identityprovider.entity;

import javax.persistence.*;



@Entity
@Table(name="CLIENT")
public class Client {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long userId;

    @Column
    private String username;

    @Column
    private String password;

    protected Client() {}

    public Client(Long userId, String username, String password){
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format (
                "User[userId=%d, username=%s, password=%s]",
                userId, username, password);
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
