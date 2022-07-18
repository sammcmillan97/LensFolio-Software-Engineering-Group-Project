package nz.ac.canterbury.seng302.portfolio.model;

public class PlannerDailyEvent {

    public String Id;
    public String date;
    public String description;
    public int numberOfEvents;
    public String type;
    public String classNames;

    public PlannerDailyEvent(String id, String date, String description, int numberOfEvents, String classNames) {
        this.Id = id;
        this.date = date;
        this.description = description + "\n";
        this.numberOfEvents = numberOfEvents;
        this.type = classNames;
    }

    public String getId() {
        return Id;
    }

    public String getDate() {
        return date;
    }

    public void addNumberOfEvents() {
        this.numberOfEvents += 1;
    }

    public void addDescription(String description) {
        this.description = this.description + description + "\n";
    }
}
