package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity // this is an entity, assumed to be in a table called Project
@Table(name="PROJECT")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String projectName;
    private String projectDescription;
    private Date projectStartDate;
    private Date projectEndDate;

    public Project() {
        Calendar cal = Calendar.getInstance();
        projectName = String.format("Project %d", cal.get(Calendar.YEAR));
        projectStartDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 8);
        projectEndDate = new Date(cal.getTimeInMillis());
        projectDescription = "";
    }

    public Project(String projectName, String projectDescription, Date projectStartDate, Date projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = projectStartDate;
        this.projectEndDate = projectEndDate;
    }

    public Project(String projectName, String projectDescription, String projectStartDate, String projectEndDate) {
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.projectStartDate = Project.stringToDate(projectStartDate);
        this.projectEndDate = Project.stringToDate(projectEndDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Project[id=%d, projectName='%s', projectStartDate='%s', projectEndDate='%s', projectDescription='%s']",
                id, projectName, projectStartDate, projectEndDate, projectDescription);
    }

    /**
     * Gets the date form of the given date string
     *
     * @param dateString the string to read as a date in format 01/Jan/2000
     * @return the given date, as a date object
     */
    public static Date stringToDate(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MMM/yyyy").parse(dateString);
        } catch (Exception e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        return date;
    }

    /**
     * Gets the string form of the given date in a readable format
     *
     * @param date the date to convert
     * @return the given date, as a string in format 01/Jan/2000
     */
    public static String dateToString(Date date) {
        return new SimpleDateFormat("dd/MMM/yyyy").format(date);
    }

    /**
     * Gets the string form of the given date in the format specified by pattern
     *
     * @param date The date from which to get a string representation
     * @param pattern The format in which to return the given date string
     * @return The given date in string format as specified by pattern
     * @see SimpleDateFormat
     */
    public static String dateToString(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /* Getters/Setters */

    public int getId(){
        return id;
    }

    public String getName() {
        return projectName;
    }

    public void setName(String newName) {
        this.projectName = newName;
    }

    public String getDescription(){
        return projectDescription;
    }

    public void setDescription(String newDescription) {
        this.projectDescription = newDescription;
    }

    /* Dates have string get/set methods to interact with view */

    public Date getStartDate() {
        return projectStartDate;
    }

    public String getStartDateString() {
        return Project.dateToString(this.projectStartDate);
    }

    public void setStartDate(Date newStartDate) {
        this.projectStartDate = newStartDate;
    }

    public void setStartDateString(String date) {
        this.projectStartDate = Project.stringToDate(date);
    }

    public Date getEndDate() {
        return projectEndDate;
    }

    public String getEndDateString() {
        return Project.dateToString(this.projectEndDate);
    }

    public void setEndDate(Date newEndDate) {
        this.projectEndDate = newEndDate;
    }

    public void setEndDateString(String date) {
        this.projectEndDate = Project.stringToDate(date);
    }

    public String getStartDateCalendarString() {return  Project.dateToString(this.projectStartDate, "yyyy-MM-dd"); }

    /**
     * Calculates the day after the end date as a calendar string
     * This is for the FullCalendar program as the end date on there is not inclusive
     * @return the day after the projects end date as a calendar string
     */
    public String getDayAfterEndDateCalendarString() {
        Calendar tempEndDate = Calendar.getInstance();
        tempEndDate.setTime(this.getEndDate());
        tempEndDate.add(Calendar.DATE, 1);
        return  Project.dateToString(tempEndDate.getTime(), "yyyy-MM-dd");
    }

    public String getEndDateCalendarString() {
        return  Project.dateToString(this.projectEndDate, "yyyy-MM-dd"); }
}
