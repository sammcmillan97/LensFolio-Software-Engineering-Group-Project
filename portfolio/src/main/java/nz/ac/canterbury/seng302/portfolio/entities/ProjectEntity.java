package nz.ac.canterbury.seng302.portfolio.entities;

import net.bytebuddy.implementation.bind.annotation.Default;

import javax.persistence.*;
import java.sql.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "project_name", nullable = false, length = 50)
    private String projectName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    public ProjectEntity() {
        Calendar cal = Calendar.getInstance();
        projectName = String.format("Project %d", cal.get(Calendar.YEAR));
        startDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 8);
        endDate = new Date(cal.getTimeInMillis());
        description = "";
    }

    public ProjectEntity(Long projectId, String projectName, String description, Date startDate, Date endDate) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ProjectEntity(String projectName, String description, Date startDate, Date endDate) {
        this.projectName = projectName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getProject_id() {
        return projectId;
    }

    public void setProject_id(Long project_id) {
        this.projectId = project_id;
    }

    public String getProject_name() {
        return projectName;
    }

    public void setProject_name(String project_name) {
        this.projectName = project_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart_date() {
        return dateToWords(startDate);
    }

    public void setStart_date(Date start_date) {
        this.startDate = start_date;
    }

    public String getEnd_date() {
        return dateToWords(endDate);
    }

    public void setEnd_date(Date end_date) {
        this.endDate = end_date;
    }

    private String dateToWords(Date date) {
        DateFormat format2 = new SimpleDateFormat("MMMMM dd, yyyy");
        return format2.format(date);
    }
}