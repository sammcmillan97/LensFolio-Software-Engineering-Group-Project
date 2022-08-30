package nz.ac.canterbury.seng302.portfolio.controller.evidence;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Categories;
import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.group.Group;
import nz.ac.canterbury.seng302.portfolio.model.project.Project;
import nz.ac.canterbury.seng302.portfolio.model.user.User;
import nz.ac.canterbury.seng302.portfolio.service.evidence.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupRepositorySettingsService;
import nz.ac.canterbury.seng302.portfolio.service.group.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.project.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.user.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The controller for handling backend of the add evidence page
 */
@Controller
public class AddEvidenceController {

    private static final String ADD_EVIDENCE = "templatesEvidence/addEvidence";
    private static final String PORTFOLIO_REDIRECT = "redirect:/portfolio";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private GroupRepositorySettingsService groupRepositorySettingsService;

    @Autowired
    private GroupsClientService groupsService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    @Autowired
    private EvidenceService evidenceService;

    private static final String TIMEFORMAT = "yyyy-MM-dd";

    private static final Logger PORTFOLIO_LOGGER = LoggerFactory.getLogger("com.portfolio");

    /**
     * Display the add evidence page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The add evidence page.
     */
    @GetMapping("/editEvidence-{evidenceId}")
    public String addEvidence(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("evidenceId") String evidenceId,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int userId = userService.getUserId(principal);
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        if (projectId == -1) {
            model.addAttribute("errorMessage", "Please select a project first");
            return PORTFOLIO_REDIRECT;
        }
        Project project = projectService.getProjectById(projectId);

        try {
            Evidence evidence = getEvidenceById(evidenceId, userId, projectId);
            addEvidenceToModel(model, projectId, userId, evidence);
            model.addAttribute("minEvidenceDate", Project.dateToString(project.getStartDate(), TIMEFORMAT));
            model.addAttribute("maxEvidenceDate", Project.dateToString(project.getEndDate(), TIMEFORMAT));
            model.addAttribute("evidenceId", Integer.parseInt(evidenceId));
            return ADD_EVIDENCE;
        } catch (IllegalArgumentException e) {
            return PORTFOLIO_REDIRECT;
        }
    }

    /**
     * Save a piece of evidence. It will be rejected silently if the data given is invalid, otherwise it will be saved.
     * If saved, the user will be taken to their portfolio page.
     * @param principal Authentication state of client
     * @param title The title of the piece of evidence
     * @param description The description of the piece of evidence
     * @param dateString The date the evidence occurred, in yyyy-MM-dd string format
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return A redirect to the portfolio page, or staying on the add evidence page
     */
    @PostMapping("/editEvidence-{evidenceId}")
    public String saveEvidence(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("evidenceId") String evidenceId,
            @RequestParam(name="evidenceTitle") String title,
            @RequestParam(name="evidenceDescription") String description,
            @RequestParam(name="evidenceDate") String dateString,
            @RequestParam(name="isQuantitative", required = false) String isQuantitative,
            @RequestParam(name="isQualitative", required = false) String isQualitative,
            @RequestParam(name="isService", required = false) String isService,
            @RequestParam(name="evidenceSkills") String skills,
            @RequestParam(name="skillsToChange") String skillsToChange,
            @RequestParam(name="evidenceUsers") String users,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        int projectId = portfolioUserService.getUserById(user.getId()).getCurrentProject();
        Project project = projectService.getProjectById(projectId);

        model.addAttribute("user", user);
        model.addAttribute("minEvidenceDate", Project.dateToString(project.getStartDate(), TIMEFORMAT));
        model.addAttribute("maxEvidenceDate", Project.dateToString(project.getEndDate(), TIMEFORMAT));

        Date date;
        try {
            date = new SimpleDateFormat(TIMEFORMAT).parse(dateString);
        } catch (ParseException exception) {
            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }

        Set<Categories> categories = new HashSet<>();
        if (isQuantitative != null) {
            categories.add(Categories.QUANTITATIVE);
        }
        if (isQualitative != null) {
            categories.add(Categories.QUALITATIVE);
        }
        if (isService != null) {
            categories.add(Categories.SERVICE);
        }

        int userId = user.getId();
        evidenceService.updateEvidenceSkills(userId, projectId, skillsToChange);
        Evidence evidence = getEvidenceById(evidenceId, userId, projectId);
        evidence.setTitle(title);
        evidence.setDescription(description);
        evidence.setSkills(skills);
        evidence.setDate(date);
        evidence.setCategories(categories);

        try {
            evidenceService.saveEvidence(evidence);
        } catch (IllegalArgumentException exception) {
            if (Objects.equals(exception.getMessage(), "Title not valid")) {
                model.addAttribute("titleError", "Title cannot be all special characters");
            } else if (Objects.equals(exception.getMessage(), "Description not valid")) {
                model.addAttribute("descriptionError", "Description cannot be all special characters");
            } else if (Objects.equals(exception.getMessage(), "Date not valid")) {
                model.addAttribute("dateError", "Date must be within the project dates");
            } else if (Objects.equals(exception.getMessage(), "Skills not valid")) {
                model.addAttribute("skillsError", "Skills cannot be more than 50 characters long");
            } else {
                model.addAttribute("generalError", exception.getMessage());
            }
            addEvidenceToModel(model, projectId, userId, evidence);
            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }
        return PORTFOLIO_REDIRECT;
    }

    /**
     * Gets a piece of evidence based on an id in string form.
     * If the id is valid, that evidence is returned. If it is -1, a new piece is returned.
     * Else, an error is thrown.
     * @param evidenceId The id of the evidence
     * @param userId The id of the user who the evidence belongs to
     * @param projectId The id of the project the evidence belongs to
     * @return A piece of evidence
     */
    private Evidence getEvidenceById(String evidenceId, int userId, int projectId) {
        try {
            int id = Integer.parseInt(evidenceId);
            if (id == -1) {
                Date evidenceDate;
                Date currentDate = new Date();
                Project project = projectService.getProjectById(projectId);
                if (currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) {
                    evidenceDate = currentDate;
                } else {
                    evidenceDate = project.getStartDate();
                }
                return new Evidence(userId, projectId, "", "", evidenceDate);
            } else {
                Evidence evidence = evidenceService.getEvidenceById(id);
                if (userId != evidence.getOwnerId()) {
                    String errorMessage = "User " + userId + " tried to access evidence they did not own";
                    PORTFOLIO_LOGGER.error(errorMessage);
                    throw new IllegalArgumentException();
                }
                return evidence;
            }
        } catch (NumberFormatException e) {
            String errorMessage = "Not a number id in add evidence get request: " + evidenceId;
            PORTFOLIO_LOGGER.error(errorMessage);
            throw new IllegalArgumentException();
        } catch (NoSuchElementException e) {
            String errorMessage = "Non-existent id in add evidence get request: " + evidenceId;
            PORTFOLIO_LOGGER.error(errorMessage);
            throw new IllegalArgumentException();
        }
    }

    /**
     * Adds helpful evidence related variables to the model.
     * They are a title, description, date, and a list of all skills for the user.
     * @param model The model to add things to
     * @param projectId The project currently being viewed
     * @param userId The logged-in user
     * @param evidence The evidence that is being viewed.
     */
    private void addEvidenceToModel(Model model, int projectId, int userId, Evidence evidence) {
        List<Evidence> evidenceList = evidenceService.getEvidenceForPortfolio(userId, projectId);
        model.addAttribute("skillsList", evidenceService.getSkillsFromEvidence(evidenceList));
        model.addAttribute("evidenceTitle", evidence.getTitle());
        model.addAttribute("evidenceDescription", evidence.getDescription());
        model.addAttribute("evidenceDate", Project.dateToString(evidence.getDate(), TIMEFORMAT));
        model.addAttribute("evidenceSkills", String.join(" ", evidence.getSkills()) + " ");
        model.addAttribute("users", userService.getAllUsersExcept(userId));
        List<Group> groups = groupsService.getAllGroups().getGroups();
        List<Group> userGroups = new ArrayList<>();
        for (Group group : groups) {
            for (User user : group.getMembers()) {
                if (user.getId() == userId) {
                    userGroups.add(group);
                }
            }
        }
        model.addAttribute("groups", userGroups);
        model.addAttribute("displayCommits", !userGroups.isEmpty());
    }

    /**
     * A method which deletes the evidence based on its id.
     * @return the portfolio page of the user
     */
    @DeleteMapping(value = "/addEvidence-{evidenceId}")
    public String deleteEvidenceById(
            @PathVariable(name="evidenceId") String evidenceId) {
        int id = Integer.parseInt(evidenceId);
        evidenceService.deleteById(id);
        return PORTFOLIO_REDIRECT;
    }
}

