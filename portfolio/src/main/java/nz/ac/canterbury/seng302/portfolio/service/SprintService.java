package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class SprintService {
    @Autowired
    private SprintRepository repository;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectEditsService projectEditsService;

    /**
     * Get list of all sprints
     */
    public List<Sprint> getAllSprints() {
        return (List<Sprint>) repository.findAll();
    }

    /**
     * Get sprint by id
     */
    public Sprint getSprintById(Integer id) throws NoSuchElementException {
        Optional<Sprint> sprint = repository.findById(id);
        if(sprint.isPresent()) {
            return sprint.get();
        }
        else
        {
            throw new NoSuchElementException("Sprint not found");
        }
    }

    /**
     * Method for returning a map of all sprints linked to project selection page.
     */
    public Map<Integer, List<Sprint>> getAllByParentProjectId() {
        List<Project> projects = projectService.getAllProjects();

        Map<Integer, List<Sprint>> sprintsByParentProject = new HashMap<>();
        for (Project project : projects) {
            int projectId = project.getId();
            sprintsByParentProject.put(projectId, getByParentProjectId(projectId));
        }

        return sprintsByParentProject;
    }

    /**
     * Method for getting all sprints in chronological order
     * @param projectId The parent project of the sprints
     * @return List of sprints in chronological order
     */
    public List<Sprint> getSprintsByProjectInOrder(int projectId) {
        List<Sprint> sprints = getByParentProjectId(projectId);
        Comparator<Sprint> comparator = Comparator.comparing(Sprint::getStartDate);
        sprints.sort(comparator);
        return sprints;
    }

    /**
     * Get a list of sprints by parent project ID
     * @param projectId The parent project of the sprints
     * @return List of sprints
     */
    public List<Sprint> getByParentProjectId(int projectId) {
        return repository.findByParentProjectId(projectId);
    }

    /**
     * Saves a sprint to the repository
     */
    public void saveSprint(Sprint sprint) {
        projectEditsService.refreshProject(sprint.getParentProjectId());
        repository.save(sprint);
    }

    /**
     * Deletes a sprint from the repository
     */
    public void deleteById(int sprintId) {
        projectEditsService.refreshProject(repository.findById(sprintId).getParentProjectId());
        repository.deleteById(sprintId);
    }

    public void deleteSprint(int projectId, int sprintId) {
        deleteById(sprintId);
        updateSprintNumbers(projectId);
    }

    /**
     * For editing updating sprint values
     */
    public void editSprint(int projectId, int sprintId, String sprintName, String sprintDescription,  Date sprintStartDate,
                           Date sprintEndDate) {
        Sprint sprint;
        try {
            sprint = getSprintById(sprintId);
            sprint.setName(sprintName);
            sprint.setDescription(sprintDescription);
            sprint.setStartDate(sprintStartDate);
            sprint.setEndDate(sprintEndDate);
            saveSprint(sprint);
            updateSprintNumbers(projectId);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Proxy method for saving a new sprint
     */
    public void createNewSprint(int projectId, String sprintName, String sprintDescription, Date sprintStartDate, Date sprintEndDate) {
        saveSprint(new Sprint(projectId, sprintName, sprintDescription, sprintStartDate, sprintEndDate));
        updateSprintNumbers(projectId);
    }

    /**
     * Create a new sprint and populate it with default values to be used in the sprint form placeholder values
     */
    public Sprint createDefaultSprint(int parentProjectId) {
        Date defaultStartDate = getDefaultSprintStartDate(parentProjectId);
        Date defaultEndDate = getDefaultSprintEndDate(parentProjectId);
        return new Sprint(parentProjectId, "New Sprint", "", defaultStartDate, defaultEndDate);

    }


    /**
     * Gets the date 1 day after the current latest sprint or the project end date if the former is out bounds
     * @param projectId The parent project for which to find the next available date
     */
    private Date getDefaultSprintStartDate(int projectId) {
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }
        Calendar minStartDate = getCalendarDay();
        minStartDate.setTime(parentProject.getStartDate());
        List<Sprint> sprints = getSprintsByProjectInOrder(projectId);
        Calendar calSprintStartDate = getCalendarDay();
        //If first sprint in project start date
        if(sprints.isEmpty()) {
            return parentProject.getStartDate();

            // Otherwise, return the day after the current last sprint's end date if this is after the project
            // end date it will just return the project end date even though sprints may overlap
            // (could be implemented better but wasn't sure how exactly).
        } else {
            Sprint lastSprint = sprints.get(sprints.size() -1);
            calSprintStartDate.setTime(lastSprint.getEndDate());
            calSprintStartDate.add(Calendar.DATE, 1);
        }

        if (!calSprintStartDate.getTime().before(parentProject.getEndDate())) {
            return parentProject.getEndDate();
        }
        return calSprintStartDate.getTime();
    }

    /**
     * Gets the date 3 weeks after the current latest sprint or the project end date if the former is out bounds
     * @param projectId The parent project for which to find the latest available date
     */
    private Date getDefaultSprintEndDate(int projectId) {
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }
        Calendar minStartDate = getCalendarDay();
        minStartDate.setTime(parentProject.getStartDate());
        List<Sprint> sprints = getSprintsByProjectInOrder(projectId);
        Calendar calSprintEndDate = getCalendarDay();
        //If first sprint in project default end is 3 weeks after parent project start date
        if (sprints.isEmpty()) {
            calSprintEndDate.setTime(parentProject.getStartDate());
            calSprintEndDate.add(Calendar.DATE, 21);
            // Otherwise, the end date is 22 days after the current last sprint's end date if this is after the project
            // end date it will just return the project end date even though sprints may overlap
            // (could be implemented better but wasn't sure how exactly).
        } else {
            Sprint lastSprint = sprints.get(sprints.size() -1);
            calSprintEndDate.setTime(lastSprint.getEndDate());
            calSprintEndDate.add(Calendar.DATE, 22);
        }

        if (!calSprintEndDate.getTime().before(parentProject.getEndDate())) {
            return parentProject.getEndDate();
        }
        return calSprintEndDate.getTime();
    }

    /**
     * Method to return a calendar object representing the very beginning of a day
     * @return Calendar object
     */
    private Calendar getCalendarDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.AM_PM, 0);
        return cal;
    }

    /**
     * Checks that the selected start date does not fall within current sprint dates
     * @param sprintId The sprint ID of the sprint being checked
     * @param projectId THe parent project ID
     * @param startDate The selected start date
     * @return String "" if it fine or "An error message if it's not
     */
    public String checkSprintStartDate(int sprintId, int projectId, Date  startDate) {
        List<Sprint> sprints = getByParentProjectId(projectId);
        for(Sprint sprint: sprints) {
            if(sprint.getId() != sprintId && !(startDate.before(sprint.getStartDate())) && !(startDate.after(sprint.getEndDate()))) {
                return "Start date is currently inside sprint '" + sprint.getName() + "': " +
                        sprint.getStartDateString() + "-" + sprint.getEndDateString();
            }
        }
        return "";
    }

    /**
     * Checks that the selected end date does not fall within current sprint dates
     * @param sprintId The sprint ID of the sprint being checked
     * @param projectId The parent project ID
     * @param endDate The selected end date
     * @return String "" if it fine or "An error message if it's not
     */
    public String checkSprintEndDate(int sprintId, int projectId, Date endDate) {
        List<Sprint> sprints = getByParentProjectId(projectId);
        for(Sprint sprint: sprints) {
            if(sprint.getId() != sprintId && !(endDate.before(sprint.getStartDate())) && !(endDate.after(sprint.getEndDate()))) {
                return "End date is currently inside sprint '" + sprint.getName() + "': " +
                        sprint.getStartDateString() + "-" + sprint.getEndDateString();
            }
        }
        return "";
    }

    /**
     * Checks that a sprint does not encase other sprints
     * @param sprintId The sprint ID of the sprint being checked
     * @param projectId The parent project ID
     * @param startDate The selected start date
     * @param endDate The selected end date
     * @return String "" if it fine or "An error message if it's not
     */
    public String checkSprintDates(int sprintId, int projectId, Date startDate, Date endDate) {
        List<Sprint> sprints = getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            if (sprint.getId() != sprintId && !(startDate.after(sprint.getStartDate())) && !(endDate.before(sprint.getStartDate()))) {
                return "Sprints can't encase other sprints";
            }
        }
        return "";
    }

    /***
     * Updates all sprint numbers within a project called after a sprint has been edited, created or deleted
     * @param projectId The parent ID project of the sprints
     */
    private void updateSprintNumbers(int projectId) {
        List<Sprint> sprints = getSprintsByProjectInOrder(projectId);
        for (int i = 0; i < sprints.size(); i++) {
            Sprint sprint = sprints.get(i);
            sprint.setNumber(i + 1);
            saveSprint(sprint);
        }
    }

    /**
     * Validates and updates a sprint start date. Used by the sprint resizing in planner
     * @param sprintId The sprint ID of the sprint being changed
     * @param newDate The new start date
     */
    public void updateStartDate(int sprintId, Date newDate) {
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();
        String startError = checkSprintStartDate(sprintId, sprintToChange.getParentProjectId(), newDate);
        String encaseError = checkSprintDates(sprintId, sprintToChange.getParentProjectId(), newDate, sprintToChange.getEndDate());
        if (!Objects.equals(startError, "") || !Objects.equals(encaseError, "")) {
                throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
            }
        if (newDate.compareTo(sprintToChange.getEndDate()) > 0) {
            throw new UnsupportedOperationException("Sprint start date must not be after end date");
        } else if (newDate.compareTo(projectStartDate) < 0 || newDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException(("Sprint start date must be within project dates"));
        } else {
            sprintToChange.setStartDate(newDate);
            saveSprint(sprintToChange);
        }
    }

    /**
     *Validates and updates a sprint end date. Used by the sprint resizing in planner
     * @param sprintId The sprint ID of the sprint being changed
     * @param newDate The new end date
     */
    public void updateEndDate(int sprintId, Date newDate) {
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();
        String endError = checkSprintEndDate(sprintId, sprintToChange.getParentProjectId(), newDate);
        String encaseError = checkSprintDates(sprintId, sprintToChange.getParentProjectId(), sprintToChange.getStartDate(), newDate);
        if (!Objects.equals(endError, "") || !Objects.equals(encaseError, "")) {
            throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
        }
        if (newDate.compareTo(sprintToChange.getStartDate()) < 0) {
            throw new UnsupportedOperationException("Sprint end date must not be before start date");
        } else if (newDate.compareTo(projectStartDate) < 0 || newDate.compareTo(projectEndDate) > 0) {
            throw new UnsupportedOperationException(("Sprint end date must be within project dates"));
        } else {
            sprintToChange.setEndDate(newDate);
            saveSprint(sprintToChange);
        }
    }
}
