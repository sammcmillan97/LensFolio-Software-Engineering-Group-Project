package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    public List<Date> getDateRestrictions(int projectId) {
        List<Sprint> sprints = sprintService.getByParentProjectId(projectId);
        List<Event> events = eventService.getByEventParentProjectId(projectId);
        List<Milestone> milestones = milestoneService.getByMilestoneParentProjectId(projectId);
        List<Deadline> deadlines = deadlineService.getByDeadlineParentProjectId(projectId);
        List<Evidence> evidences = evidenceService.getEvidenceByProjectId(projectId);

        List<Date> startDates = new ArrayList<>();
        List<Date> endDates = new ArrayList<>();

        for (Sprint sprint : sprints){
            startDates.add(sprint.getStartDate());
            endDates.add(sprint.getEndDate());
        }
        for (Event event : events) {
            startDates.add(event.getEventStartDate());
            endDates.add(event.getEventEndDate());
        }
        for (Milestone milestone : milestones) {
            startDates.add(milestone.getMilestoneDate());
            endDates.add(milestone.getMilestoneDate());
        }
        for (Deadline deadline : deadlines) {
            startDates.add(deadline.getDeadlineDate());
            endDates.add(deadline.getDeadlineDate());
        }
        for (Evidence evidence : evidences) {
            startDates.add(evidence.getDate());
            endDates.add(evidence.getDate());
        }

        if (startDates.isEmpty()) {
            return new ArrayList<>();
        }

        Date firstStartDate = startDates.get(0);
        for (Date startDate: startDates) {
            if (startDate.before(firstStartDate)) {
                firstStartDate = startDate;
            }
        }

        Date lastEndDate = endDates.get(0);
        for (Date endDate: endDates) {
            if (endDate.after(lastEndDate)) {
                lastEndDate = endDate;
            }
        }
        List<Date> restrictedDates = new ArrayList<>();
        restrictedDates.add(firstStartDate);
        restrictedDates.add(lastEndDate);
        return restrictedDates;
    }

}
