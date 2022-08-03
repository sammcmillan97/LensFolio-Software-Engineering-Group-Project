package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called evidence
@Table(name="EVIDENCE")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int ownerId; // ID of the user who owns this evidence piece
    private int projectId; // ID of the project this evidence relates to
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private Date date;

    public Evidence() {}

    public Evidence(int ownerId, int projectId, String title, String description, Date date) {
        this.ownerId = ownerId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

}
