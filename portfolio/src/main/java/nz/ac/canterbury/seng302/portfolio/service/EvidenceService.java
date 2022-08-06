package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EvidenceService {

    @Autowired
    private EvidenceRepository repository;

    @Autowired
    private ProjectService projectService;

    /**
     * Get list of all pieces of evidence for a specific portfolio.
     * Portfolios can be identified by a user and project.
     * @param userId The user of this portfolio
     * @param projectId The project for this portfolio
     * @return A list of all evidence relating to this portfolio. It is ordered chronologically.
     */
    public List<Evidence> getEvidenceForPortfolio(int userId, int projectId) {
        List<Evidence> evidence = repository.findByOwnerIdAndProjectId(userId, projectId);
        evidence.sort(Comparator.comparing(Evidence::getDate));
        Collections.reverse(evidence);
        return evidence;
    }

    /**
     * Get a specific piece of evidence by ID
     */
    public Evidence getEvidenceById(Integer id) throws NoSuchElementException {
        Optional<Evidence> evidence = repository.findById(id);
        if(evidence.isPresent()) {
            return evidence.get();
        }
        else
        {
            throw new NoSuchElementException("Evidence not found");
        }
    }

    public List<Evidence> getEvidenceByProjectId(int projectId) {
        return repository.findByProjectId(projectId);
    }

    /**
     * Saves a piece of evidence. Makes sure the project the evidence is for exists.
     * ALso makes sure the description and title are not one character, and contain at least one letter.
     * Also makes sure the project start and end dates are within the project bounds.
     * @param evidence The evidence to save
     */
    public void saveEvidence(Evidence evidence) {
        Project project;
        try {
            project = projectService.getProjectById(evidence.getProjectId());
        } catch (NoSuchElementException exception) {
            throw new IllegalArgumentException("Project does not exist");
        }
        Pattern fieldPattern = Pattern.compile("[a-zA-Z]+");
        Matcher titleMatcher = fieldPattern.matcher(evidence.getTitle());
        if (!titleMatcher.find() || evidence.getTitle().length() < 2 || evidence.getTitle().length() > 64) {
            throw new IllegalArgumentException("Title not valid");
        }
        Matcher descriptionMatcher = fieldPattern.matcher(evidence.getDescription());
        if (!descriptionMatcher.find() || evidence.getDescription().length() < 50 || evidence.getDescription().length() > 1024) {
            throw new IllegalArgumentException("Description not valid");
        }
        if (project.getStartDate().after(evidence.getDate()) || project.getEndDate().before(evidence.getDate())) {
            throw new IllegalArgumentException("Date not valid");
        }
        for (String skill : evidence.getSkills()) {
            if (skill.length() > 50) {
                throw new IllegalArgumentException("Skills not valid");
            }
        }
        List<Evidence> evidenceList = repository.findByOwnerIdAndProjectId(evidence.getOwnerId(), evidence.getProjectId());
        evidence.conformSkills(getSkillsFromEvidence(evidenceList));
        repository.save(evidence);
    }

    /**
     * Gets all skills from a list of evidence. Each skill returned is unique.
     * @param evidenceList A list of evidence to retrieve skills from.
     * @return All the skills for that list of evidence.
     */
    public Collection<String> getSkillsFromEvidence(List<Evidence> evidenceList) {
        Collection<String> skills = new HashSet<>();
        for (Evidence userEvidence : evidenceList) {
            skills.addAll(userEvidence.getSkills());
        }
        return skills;
    }

    /**
     * Deletes a piece of evidence.
     * @param id The ID of the evidence to delete
     */
    public void deleteById(int id) {
        repository.deleteById(id);
    }

    /**
     * Saves a web link string to the evidence specified by evidenceId.
     * @param evidenceId The evidence to have the web link added to.
     * @param weblink The web link sting to be added to evidence of id=evidenceId.
     * @throws NoSuchElementException If evidence specified by evidenceId does not exist NoSuchElementException
     * is thrown.
     */
    public void saveWebLink(int evidenceId, String weblink) throws NoSuchElementException {
        try {
            Evidence evidence = getEvidenceById(evidenceId);
            evidence.addWebLink(weblink);
            saveEvidence(evidence);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Evidence not found: web link not saved");
        }
    }

    /**
     * Wrote a method for retrieve evidence by skill
     * @param skill being searched for
     * @return list of evidences containing skill
     */
    public List<Evidence> retrieveEvidenceBySkill(String skill) {
        return repository.findBySkills(skill);
    }

}