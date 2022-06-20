package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity // this is an entity, assumed to be in a table called Sprint
@Table(name="SPRINT")
public class Sprint implements ImportantDate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int parentProjectId;
    private String sprintName;
    private int sprintNumber;
    private String sprintDescription;
    private Date sprintStartDate;
    private Date sprintEndDate;
    @Transient
    private List<Integer> eventsInside = new ArrayList<>();
    @Transient
    private List<Integer> deadlinesInside = new ArrayList<>();
    @Transient
    private String colour;

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

    /**
     * Gets the string form of the given date in the FullCalendar format
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    static String dateToCalenderString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
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
        this.sprintEndDate = Project.stringToDate(date);
    }

    public String getStartDateCalendarString() {return  Project.dateToString(this.sprintStartDate, "yyyy-MM-dd"); }

    /**
     * Calculates the day after the end date as a calendar string
     * This is for the FullCalendar program as the end date on there is not inclusive
     * @return the day after the sprints end date as a calendar string
     */
    public String getDayAfterEndDateCalendarString() {
        Calendar tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(this.getEndDate());
        tempEndDate.add(Calendar.DATE, 1);
        return  Project.dateToString(tempEndDate.getTime(), "yyyy-MM-dd"); }

    public void setEndDateCalendar(Date newEndDate) {
        Calendar tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(newEndDate);
        tempEndDate.add(Calendar.DATE, -1);
        this.sprintEndDate = tempEndDate.getTime();
    }

    public List<Integer> getEventsInside() {
        return eventsInside;
    }

    public void addEventsInside(int eventIndex) {
        eventsInside.add(eventIndex);
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void addDeadlinesInside(int deadlineIndex) {
        deadlinesInside.add(deadlineIndex);
    }
}
