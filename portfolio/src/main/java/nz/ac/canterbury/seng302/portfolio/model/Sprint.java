package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Sprint
@Table(name="SPRINT")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int parentProjectId;
    private String sprintName;
    private int sprintNumber;
    private String sprintDescription;
    private Date sprintStartDate;
    private Date sprintEndDate;

    public Sprint() {}

    public Sprint(int parentProjectId, String sprintName, int sprintNumber, String sprintDescription, Date sprintStartDate, Date sprintEndDate) {
        this.parentProjectId = parentProjectId;
        this.sprintName = sprintName;
        this.sprintNumber = sprintNumber;
        this.sprintDescription = sprintDescription;
        this.sprintStartDate = sprintStartDate;
        this.sprintEndDate = sprintEndDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint[id=%d, parentProjectId='%d', sprintName='%s', sprintLabel='%s', sprintStartDate='%s', sprintEndDate='%s', sprintDescription='%s']",
                id, parentProjectId, sprintName, "Sprint " + sprintNumber, sprintStartDate, sprintEndDate, sprintDescription);
    }


    public int getId(){
        return  id;
    }
    public int getParentProjectId() {
        return parentProjectId;
    }
    public String getName() {
        return sprintName;
    }
    public void setName(String name) {
        sprintName = name;
    }
    public int getNumber() {
        return sprintNumber;
    }
    public void setNumber(int number) {
        sprintNumber = number;
    }
    public String getLabel() {
        return "Sprint " + sprintNumber;
    }
    public String getDescription(){
        return sprintDescription;
    }
    public void setDescription(String description) {
        sprintDescription = description;
    }

    public Date getStartDate() {
        return sprintStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.sprintStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.sprintStartDate = newStartDate;
    }

    public void setStartDateString(String date) {
        this.sprintStartDate = Project.stringToDate(date);
    }

    public Date getEndDate() {
        return sprintEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.sprintEndDate);
    }

    public void setEndDate(Date newEndDate) {
        this.sprintEndDate = newEndDate;
    }

    public void setEndDateString(String date) {
        this.sprintStartDate = Project.stringToDate(date);
    }
}
