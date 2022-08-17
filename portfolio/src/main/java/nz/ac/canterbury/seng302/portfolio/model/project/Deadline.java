package nz.ac.canterbury.seng302.portfolio.model.project;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Deadline
@Table(name="DEADLINE")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deadlineId;
    private int deadlineParentProjectId;
    private String deadlineName;
    private Date deadlineDate;
    @Transient
    private boolean completed;
    @Transient
    private String colour;
    
    public Deadline() {}
    
    public Deadline(int deadlineParentProjectId, String deadlineName, Date deadlineDate) {
        this.deadlineParentProjectId = deadlineParentProjectId;
        this.deadlineName = deadlineName;
        this.deadlineDate = deadlineDate;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Deadline[deadlineId=%d, deadlineParentProjectId='%d', deadlineName='%s', deadlineEndDate='%s']",
                deadlineId, deadlineParentProjectId, deadlineName, deadlineDate);
    }


    /**
     * Gets the string form of the given date in a readable format
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000 00:00:00
     */
    public static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a").format(date);
    }

    /* Getters/Setters */

    public int getDeadlineId() {
        return deadlineId;
    }

    public int getDeadlineParentProjectId() {
        return deadlineParentProjectId;
    }

    public String getDeadlineName() {
        return deadlineName;
    }

    public void setDeadlineName(String deadlineName) {
        this.deadlineName = deadlineName;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public String getDeadlineDateString() {return Project.dateToString(this.deadlineDate, "dd/MMMM/yyyy hh:mm a");}

    public void setDeadlineDate(Date deadlineEndDate) {
        this.deadlineDate = deadlineEndDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getDateString() {
        return Project.dateToString(this.deadlineDate, "dd/MMM/yyyy hh:mm a");
    }
}
