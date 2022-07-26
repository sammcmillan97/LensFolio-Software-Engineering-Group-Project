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

    public List<Sprint> getByParentProjectId(int projectId) {
        return repository.findByParentProjectId(projectId);
    }

    public Map<Integer, List<Sprint>> getAllByParentProjectId() {
        List<Project> projects = projectService.getAllProjects();

        Map<Integer, List<Sprint>> sprintsByParentProject = new HashMap<>();
        for (Project project : projects) {
            int projectId = project.getId();
            sprintsByParentProject.put(projectId, getByParentProjectId(projectId));
        }

        return sprintsByParentProject;
    }

    public void saveSprint(Sprint sprint) {
        projectEditsService.refreshProject(sprint.getParentProjectId());
        repository.save(sprint);
    }

    public void deleteById(int sprintId) {
        projectEditsService.refreshProject(repository.findById(sprintId).getParentProjectId());
        repository.deleteById(sprintId);
    }

    /**
     * Create a new sprint and populate it with default values for the sprint form
     * @param parentProjectId
     * @return
     */
    public Sprint createDefaultSprint(int parentProjectId) {
        int sprintNumber = getNextSprintNumber(parentProjectId);
        Date defaultStartDate = getDefaultSprintStartDate(parentProjectId, sprintNumber);
        Date defaultEndDate = getDefaultSprintEndDate(parentProjectId, sprintNumber);
        return new Sprint(parentProjectId, "New Sprint", sprintNumber, "", defaultStartDate, defaultEndDate);
    }

    public void updateStartDate(int sprintId, Date newDate) {
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();
        List<Sprint> sprints = getByParentProjectId(sprintToChange.getParentProjectId());
        sprints.sort(Comparator.comparing(Sprint::getNumber));

        for (Sprint sprint :sprints) {
            if ((sprint.getNumber() < sprintToChange.getNumber()) && (newDate.compareTo(sprint.getEndDate()) <= 0)) {
                throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
            }
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

    public void updateEndDate(int sprintId, Date newDate) {
        Sprint sprintToChange = getSprintById(sprintId);
        Date projectStartDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getStartDate();
        Date projectEndDate = projectService.getProjectById(sprintToChange.getParentProjectId()).getEndDate();
        List<Sprint> sprints = getByParentProjectId(sprintToChange.getParentProjectId());
        sprints.sort(Comparator.comparing(Sprint::getNumber));

        for (Sprint sprint :sprints) {
            if ((sprint.getNumber() > sprintToChange.getNumber()) && (newDate.compareTo(sprint.getStartDate()) >= 0)) {
                throw new UnsupportedOperationException(("Sprint must not be within another sprint"));
            }
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

    /**
     * Gets the next sprint number for a given project
     * @param projectId The parent project for which to find the next sprint number
     * @return The sprint number of the project's next sprint
     */
    private int getNextSprintNumber(int projectId) {
        // Number of first sprint is 1
        int nextSprintNumber = 1;

        // If there are any sprints with sprint number equal or greater to current sprint number
        // set nextSprintNumber one greater
        List<Sprint> sprints = getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            int sprintNumber = sprint.getNumber();
            if (sprintNumber >= nextSprintNumber) {
                nextSprintNumber = sprintNumber + 1;
            }
        }
        return nextSprintNumber;
    }


    /**
     * Gets the soonest available date occurring after all of a project's sprints that occur before a given sprint
     * @param projectId The parent project for which to find the next available date
     * @param sprintNumber The position of the sprint in question in the order of a project's sprints
     * @return The soonest available date occurring after all the project's sprints occurring before the given sprint
     */
    private Date getDefaultSprintStartDate(int projectId, int sprintNumber) {
        // Try to find project matching id
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }

        // Set min start initially to one day after project start date
        Calendar minStartDate = getCalendarDay();
        minStartDate.setTime(parentProject.getStartDate());

        // If there are any sprints before the given sprint, set min start to one day after latest end
        List<Sprint> sprints = getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints after the given sprint
            if (sprint.getNumber() >= sprintNumber) {
                continue;
            }
            Date endDate = sprint.getEndDate();
            Calendar calDate = getCalendarDay();
            calDate.setTime(endDate);
            // If min start date is on or before the current sprint's end date
            if (!minStartDate.after(calDate)) {
                minStartDate.setTime(endDate);
                minStartDate.add(Calendar.DATE, 1);
            }
        }
        return minStartDate.getTime();
    }

    /**
     * Gets the latest available date occurring before any following sprints begin, or the project ends.
     * @param projectId The parent project for which to find the latest available date
     * @param sprintNumber The position of the sprint in question in the order of a project's sprints
     * @return The latest available date occurring before any following sprints begin, or the project ends
     */
    private Date getDefaultSprintEndDate(int projectId, int sprintNumber) {
        // Try to find project matching id
        Project parentProject;
        try {
            parentProject = projectService.getProjectById(projectId);
        } catch (Exception e) {
            return null;
        }

        // Set max end initially to project end date
        Calendar maxEndDate = getCalendarDay();
        maxEndDate.setTime(parentProject.getEndDate());

        // If there are any sprints after the given sprint, set max end date to day before next sprint start date
        List<Sprint> sprints = getByParentProjectId(projectId);
        for (Sprint sprint : sprints) {
            // Skip sprints before the given sprint
            if (sprintNumber >= sprint.getNumber()) {
                continue;
            }

            Date startDate = sprint.getStartDate();
            Calendar startCal = getCalendarDay();
            startCal.setTime(startDate);
            // If max end date is after the current sprint's start date
            if (maxEndDate.after(startCal)) {
                maxEndDate.setTime(startDate);
                maxEndDate.add(Calendar.DATE, -1);
            }
        }
        return maxEndDate.getTime();
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
}
