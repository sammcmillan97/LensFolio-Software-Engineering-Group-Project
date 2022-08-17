package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.project.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectDateService {

    @Autowired
    private SprintService sprintService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private EvidenceService evidenceService;

    /**
     * Gets date restrictions for a project. Date restrictions tell the rest of the application the first and last day
     * that anything happens on in the project, to prevent users from changing project dates to something which does
     * not make sense. Also, a text field is provided so the frontend can display why the user is restricted from
     * changing the project dates.
     * This method is (as of writing this) only needed for when the user wants to change project dates.
     * @param projectId The id of the project we are querying restrictions from
     * @return A DateRestrictions class representing the date restrictions for the given project
     */
    public DateRestrictions getDateRestrictions(int projectId) {
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        List<Event> events = eventService.getByEventParentProjectId(projectId);
        List<Milestone> milestones = milestoneService.getByMilestoneParentProjectId(projectId);
        List<Deadline> deadlines = deadlineService.getByDeadlineParentProjectId(projectId);
        List<Evidence> evidences = evidenceService.getEvidenceByProjectId(projectId);

        List<DateRestriction> startDates = new ArrayList<>();
        List<DateRestriction> endDates = new ArrayList<>();

        for (Sprint sprint : sprints) {
            startDates.add(new DateRestriction(sprint.getStartDate(), "a sprint"));
            endDates.add(new DateRestriction(sprint.getEndDate(), "a sprint"));
        }
        for (Event event : events) {
            startDates.add(new DateRestriction(event.getEventStartDate(), "an event"));
            endDates.add(new DateRestriction(event.getEventEndDate(), "an event"));
        }
        for (Milestone milestone : milestones) {
            startDates.add(new DateRestriction(milestone.getMilestoneDate(), "a milestone"));
            endDates.add(new DateRestriction(milestone.getMilestoneDate(), "a milestone"));
        }
        for (Deadline deadline : deadlines) {
            startDates.add(new DateRestriction(deadline.getDeadlineDate(), "a deadline"));
            endDates.add(new DateRestriction(deadline.getDeadlineDate(), "a deadline"));
        }
        for (Evidence evidence : evidences) {
            startDates.add(new DateRestriction(evidence.getDate(), "a user's piece of evidence"));
            endDates.add(new DateRestriction(evidence.getDate(), "a user's piece of evidence"));
        }

        if (startDates.isEmpty()) {
            return new DateRestrictions();
        }

        DateRestriction firstStartDate = startDates.get(0);
        for (DateRestriction startDate: startDates) {
            if (startDate.date().before(firstStartDate.date())) {
                firstStartDate = startDate;
            }
        }

        DateRestriction lastEndDate = endDates.get(0);
        for (DateRestriction endDate: endDates) {
            if (endDate.date().after(lastEndDate.date())) {
                lastEndDate = endDate;
            }
        }
        return new DateRestrictions(firstStartDate.date(), lastEndDate.date(), firstStartDate.text(), lastEndDate.text());
    }

}
