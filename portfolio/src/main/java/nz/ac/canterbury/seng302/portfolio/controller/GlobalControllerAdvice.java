package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.PortfolioUser;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    @ModelAttribute("allProjects")
    public List<Project> getAllProjects(){
        return projectService.getAllProjects();
    }

    @ModelAttribute("currentProject")
    public Project getCurrentProject(@AuthenticationPrincipal AuthState principal){
        int id;
        try {
            id = Integer.parseInt(principal.getClaimsList().stream()
                    .filter(claim -> claim.getType().equals("nameid"))
                    .findFirst()
                    .map(ClaimDTO::getValue)
                    .orElse("-100"));

            PortfolioUser user = portfolioUserService.getUserById(id);
            return portfolioUserService.getCurrentProject(user.getUserId());
        } catch (Exception e) {
            if (projectService.getAllProjects().isEmpty()){
                return new Project();
            } else {
                return projectService.getAllProjects().get(0);
            }
        }
    }
}
