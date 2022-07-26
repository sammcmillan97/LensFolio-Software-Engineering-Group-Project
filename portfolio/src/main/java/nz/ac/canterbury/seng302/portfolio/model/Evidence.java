package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called evidence
@Table(name="EVIDENCE")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int ownerId; // ID of the user who owns this evidence piece
    private String title;
    private String description;
    private Date date;

    public Evidence() {}

    public Evidence(int ownerId, String title, String description, Date date) {
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint[id=%d, ownerId=%d, title='%s', description='%s', date='%s']",
                id, ownerId, title, description, date);
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
