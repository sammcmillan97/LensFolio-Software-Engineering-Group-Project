package nz.ac.canterbury.seng302.portfolio.model.project;


public class PlannerDailyEvent {

    private final String id;
    private final String date;
    private String description;
    private int numberOfEvents;
    private final String type;

    public PlannerDailyEvent(String id, String date, String description, int numberOfEvents, String type) {
        this.id = id;
        this.date = date;
        this.description = description + "\n";
        this.numberOfEvents = numberOfEvents;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfEvents() {
        return numberOfEvents;
    }

    public void setNumberOfEvents(int numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    public String getType() {
        return type;
    }


    public void addNumberOfEvents() {
        this.numberOfEvents += 1;
    }

    public void addDescription(String description) {
        this.description = this.description + description + "\n";
    }

}
