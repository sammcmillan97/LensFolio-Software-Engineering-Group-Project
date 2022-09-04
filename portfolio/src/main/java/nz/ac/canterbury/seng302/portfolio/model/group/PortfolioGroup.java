package nz.ac.canterbury.seng302.portfolio.model.group;

import javax.persistence.*;

@Entity
@Table(name="PORTFOLIO_GROUP")
public class PortfolioGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    @Column(unique = true)
    private int groupId;
    private int parentProjectId;

    public PortfolioGroup(int groupId, int projectId){
        this.groupId = groupId;
        this.parentProjectId = projectId;
    }

    //Empty constructor needed for JPA
    public PortfolioGroup() {

    }

    public int getId() {
        return id;
    }

    public int getGroupId() {return groupId;}

    public void setGroupId(int groupId) {this.groupId = groupId;}

    public int getParentProjectId() {
        return parentProjectId;
    }

    public void setParentProjectId(int parentProject) {
        this.parentProjectId = parentProject;
    }
}
