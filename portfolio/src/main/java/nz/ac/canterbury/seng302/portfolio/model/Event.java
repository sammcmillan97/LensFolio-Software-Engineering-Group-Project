package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;

@Entity // this is an entity, assumed to be in a table called Event
@Table(name="EVENT")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int eventId;
    private int eventParentProjectId;
    private String eventName;
    private int eventNumber;
    private Date eventStartDate;
    private Date eventEndDate;

    public Event() {}

    public Event(int eventParentProjectId, String eventName, int eventNumber, Date eventStartDate, Date eventEndDate) {
        this.eventParentProjectId = eventParentProjectId;
        this.eventName = eventName;
        this.eventNumber = eventNumber;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Event[eventId=%d, eventParentProjectId='%d', eventName='%s', eventNumber='%s', eventStartDate='%s', eventEndDate='%s']",
                eventId, eventParentProjectId, eventName, "Event " + eventNumber, eventStartDate, eventEndDate);
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

    public int getEventNumber() {
        return eventNumber;
    }

    public void setEventNumber(int eventNumber) {
        this.eventNumber = eventNumber;
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

}
