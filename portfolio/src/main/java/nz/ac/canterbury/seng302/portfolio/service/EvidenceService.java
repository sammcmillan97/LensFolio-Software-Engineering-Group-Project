package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public void saveEvidence(Evidence evidence) {
        Project project = projectService.getProjectById(evidence.getId());
        if (!project.getStartDate().after(evidence.getDate()) && !project.getEndDate().before(evidence.getDate())) {
            repository.save(evidence);
        } else {
            throw new IllegalArgumentException("Date not valid");
        }
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

}
