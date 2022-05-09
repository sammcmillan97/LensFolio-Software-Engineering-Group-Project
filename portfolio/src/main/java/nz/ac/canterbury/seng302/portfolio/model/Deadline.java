package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Deadline
@Table(name="DEADLINE")
public class Deadline {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deadlineId;
    private int deadlineParentProjectId;
    private String deadlineName;
    private int deadlineNumber;
    private Date deadlineStartDate;
    private Date deadlineEndDate;
    
    public Deadline() {}
    
    public Deadline(int deadlineParentProjectId, String deadlineName, int deadlineNumber, Date deadlineStartDate, Date deadlineEndDate) {
        this.deadlineParentProjectId = deadlineParentProjectId;
        this.deadlineName = deadlineName;
        this.deadlineNumber = deadlineNumber;
        this.deadlineStartDate = deadlineStartDate;
        this.deadlineEndDate = deadlineEndDate;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Deadline[deadlineId=%d, deadlineParentProjectId='%d', deadlineName='%s', deadlineNumber='%s', deadlineStartDate='%s', deadlineEndDate='%s']",
                deadlineId, deadlineParentProjectId, deadlineName, "Deadline " + deadlineNumber, deadlineStartDate, deadlineEndDate);
    }

    /* Getters/Setters */

    public int getDeadlineId() {
        return deadlineId;
    }

    public int getEventParentProjectId() {
        return deadlineParentProjectId;
    }

    public String getDeadlineName() {
        return deadlineName;
    }

    public void setDeadlineName(String deadlineName) {
        this.deadlineName = deadlineName;
    }

    public int getDeadlineNumber() {
        return deadlineNumber;
    }

    public void setDeadlineNumber(int deadlineNumber) {
        this.deadlineNumber = deadlineNumber;
    }

    public Date getDeadlineStartDate() {
        return deadlineStartDate;
    }

    public void setDeadlineStartDate(Date deadlineStartDate) {
        this.deadlineStartDate = deadlineStartDate;
    }

    public Date getDeadlineEndDate() {
        return deadlineEndDate;
    }

    public void setDeadlineEndDate(Date deadlineEndDate) {
        this.deadlineEndDate = deadlineEndDate;
    }
}
