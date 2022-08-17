package nz.ac.canterbury.seng302.portfolio.model.group;

import javax.persistence.*;

@Entity
@Table(name="PORTFOLIO_GROUP")
public class PortfolioGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private int groupId;
    private int parentProject;

    public PortfolioGroup(int groupId, int projectId){
        this.groupId = groupId;
        this.parentProject = projectId;
    }

    //Empty constructor needed for JPA
    public PortfolioGroup() {

    }

    public int getId() {
        return id;
    }

    public int getGroupId() {return groupId;}

    public void setGroupId(int groupId) {this.groupId = groupId;}

    public int getParentProject() {
        return parentProject;
    }

    public void setParentProject(int parentProject) {
        this.parentProject = parentProject;
    }
}
