package nz.ac.canterbury.seng302.portfolio.entities;

import javax.persistence.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Entity
@Table(name="sprint")
public class SprintEntity {
    @ManyToOne
    @JoinColumn(name="project_id", nullable = false)
    private ProjectEntity project;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sprint_id", nullable = false)
    private Long sprintId;

    @Column(name = "sprint_label", nullable = false)
    private String sprintLabel;

    @Column(name = "sprint_name", nullable = false, length = 50)
    private String sprintName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    public SprintEntity(ProjectEntity project, String sprintLabel, String sprintName, String description, Date startDate, Date endDate) {
        this.project = project;
        this.sprintLabel = sprintLabel;
        this.sprintName = sprintName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public SprintEntity() {

    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getSprintLabel() {
        return sprintLabel;
    }

    public void setSprintLabel(String sprintLabel) {
        this.sprintLabel = sprintLabel;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return dateToWords(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return dateToWords(endDate);
    }

    private String dateToWords(Date date) {
        DateFormat format2 = new SimpleDateFormat("MMMMM dd, yyyy");
        return format2.format(date);
    }
}