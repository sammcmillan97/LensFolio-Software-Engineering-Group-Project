package nz.ac.canterbury.seng302.portfolio.model.evidence;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Entity // this is an entity, assumed to be in a table called evidence
@Table(name="EVIDENCE")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int ownerId; // ID of the user who owns this evidence piece
    private int projectId; // ID of the project this evidence relates to
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "evidence_categories",
            joinColumns =  @JoinColumn(name="id")
    )
    @Column(name="categories")
    private Set<Categories> categories = new HashSet<>();

    private Date date;
    @ElementCollection
    private List<WebLink> webLinks;
    @ElementCollection
    private List<String> skills; //skills related to this piece of evidence
    @Transient
    private Set<Integer> users = new HashSet<>(Set.of(ownerId));


    public Evidence() {
        webLinks = new ArrayList<>();
    }

    public Evidence(int ownerId, int projectId, String title, String description, Date date) {
        this(ownerId, projectId, title, description, date, "");
    }

    public Evidence(int ownerId, int projectId, String title, String description, Date date, String skills) {
        this.ownerId = ownerId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.date = date;
        webLinks = new ArrayList<>();
        this.skills = new ArrayList<>(Arrays.asList(skills.split("\\s+")));
        // If the entered string is "" or has leading spaces, the regex adds an empty element at the start of the skill list
        // which should not happen.
        if (Objects.equals(this.skills.get(0), "")) {
            this.skills.remove(0);
        }
        this.skills = this.skills.stream().distinct().collect(Collectors.toCollection(ArrayList::new));
    }


    public int getId() {
        return id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    public List<WebLink> getWebLinks() {
        return webLinks;
    }

    public void addWebLink(WebLink webLink) {
        this.webLinks.add(webLink);
    }

    public List<String> getSkills() {return skills;}

    public void addSkill (String skill) {this.skills.add(skill);}

    public Set<Integer> getUsers() {
        return users;
    }

    public void addUser(Integer userId) {
        this.users.add(userId);
    }

    /**
     * Forces skills to conform to a list of master skills.
     * If capitalization differs between a skill in this evidence and the master skills,
     * the capitalization in the master skills is preferred.
     * @param masterSkills A list of master skills to compare against.
     */
    public void conformSkills(Collection<String> masterSkills) {
        List<String> newSkills = new ArrayList<>();
        for (String skill : skills) {
            boolean inMaster = false;
            for (String masterSkill : masterSkills) {
                if (!inMaster && masterSkill.equalsIgnoreCase(skill)) {
                    newSkills.add(masterSkill);
                    inMaster = true;
                }
            }
            if (!inMaster) {
                newSkills.add(skill);
            }
        }
        skills = newSkills;
    }

    public int getNumberWeblinks() { return webLinks.size(); }

    public List<Categories> getCategories() {
        List<Categories> sortedCategories = new ArrayList<>(categories);
        Collections.sort(sortedCategories);
        return sortedCategories;
    }

    public void setCategories(Set<Categories> categories) {
        this.categories = categories;
    }
}
