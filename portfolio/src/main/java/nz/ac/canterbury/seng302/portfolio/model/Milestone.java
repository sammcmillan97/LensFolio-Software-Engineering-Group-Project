package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int milestoneParentProjectId;
    private String milestoneName;
    private Date milestoneDate;
    @Transient
    private boolean completed;
    @Transient
    private String colour;

    public Milestone() {}

    public Milestone(int milestoneParentProjectId, String milestoneName, Date milestoneDate) {
        this.milestoneParentProjectId = milestoneParentProjectId;
        this.milestoneName = milestoneName;
        this.milestoneDate = milestoneDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Milestone[id=%d, parentProjectId='%d', name='%s', date='%s']",
                id, milestoneParentProjectId, milestoneName, milestoneDate);
    }

    /* Getters/Setters */

    public int getId() {
        return id;
    }

    public int getMilestoneParentProjectId() {
        return milestoneParentProjectId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String deadlineName) {
        this.milestoneName = deadlineName;
    }

    public Date getMilestoneDate() {
        return milestoneDate;
    }

    public void setMilestoneDate(Date deadlineEndDate) {
        this.milestoneDate = deadlineEndDate;
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
}
