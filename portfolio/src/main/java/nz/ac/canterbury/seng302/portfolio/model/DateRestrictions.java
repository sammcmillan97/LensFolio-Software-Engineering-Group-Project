package nz.ac.canterbury.seng302.portfolio.model;

import java.util.Date;

/**
 * Stores date restriction information for the project date service method getDateRestrictions.
 */
public class DateRestrictions {

    private final boolean hasRestrictions;
    private final Date startDate;
    private final Date endDate;
    private final String startDateText;
    private final String endDateText;

    // Constructor for if date restrictions are not present
    public DateRestrictions() {
        this.hasRestrictions = false;
        this.startDate = null;
        this.endDate = null;
        this.startDateText = null;
        this.endDateText = null;
    }

    public DateRestrictions(Date startDate, Date endDate, String startDateText, String endDateText) {
        this.hasRestrictions = true;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startDateText = startDateText;
        this.endDateText = endDateText;
    }

    public boolean hasRestrictions() {
        return hasRestrictions;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getStartDateText() {
        return startDateText;
    }

    public String getEndDateText() {
        return endDateText;
    }

}
