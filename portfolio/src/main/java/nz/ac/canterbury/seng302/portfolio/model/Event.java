package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Event
@Table(name="EVENT")
public class Event implements ImportantDate{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int eventId;
    private int eventParentProjectId;
    private String eventName;
    private Date eventStartDate;
    private Date eventEndDate;
    @Transient
    private String type;
    @Transient
    private boolean completed;
    @Transient
    private String colourStart;
    @Transient
    private String colourEnd;

    public Event() {}

    public Event(int eventParentProjectId, String eventName, Date eventStartDate, Date eventEndDate) {
        this.eventParentProjectId = eventParentProjectId;
        this.eventName = eventName;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Event[id=%d, eventParentProjectId='%d', eventName='%s', eventStartDate='%s', eventEndDate='%s']",
                eventId, eventParentProjectId, eventName, eventStartDate, eventEndDate);
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

    public int getEventId() {
        return eventId;
    }

    public int getEventParentProjectId() {
        return eventParentProjectId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getEventStartDate() {
        return eventStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.eventStartDate, "dd/MMMM/yyyy hh:mm a");
    }

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.eventEndDate, "dd/MMMM/yyyy hh:mm a");
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getStartDateCalendarString() {return  Project.dateToString(this.eventStartDate, "yyyy-MM-dd"); }

    /**
     * Calculates the day after the end date as a calendar string
     * This is for the FullCalendar program as the end date on there is not inclusive
     * @return the day after the sprints end date as a calendar string
     */
    public String getDayAfterEndDateCalendarString() {
        Calendar tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(this.getEventEndDate());
        tempEndDate.add(Calendar.DATE, 1);
        return  Project.dateToString(tempEndDate.getTime(), "yyyy-MM-dd"); }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getColourStart() {
        return colourStart;
    }

    public void setColourStart(String colourStart) {
        this.colourStart = colourStart;
    }

    public String getColourEnd() {
        return colourEnd;
    }

    public void setColourEnd(String colourEnd) {
        this.colourEnd = colourEnd;
    }

}
