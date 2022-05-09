package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
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
                "Event[id=%d, eventParentProjectId='%d', eventName='%s', eventLabel='%s', eventStartDate='%s', eventEndDate='%s']",
                eventId, eventParentProjectId, eventName, eventStartDate, eventEndDate);
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

    public void setEventStartDate(Date eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Date getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Date eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

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
