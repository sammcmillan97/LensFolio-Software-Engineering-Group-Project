package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PortfolioUserController {

    @Autowired
    private PortfolioUserService portfolioUserService;

    @PostMapping("setCurrentProject-{id}")
    public void setCurrentProject(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable("id") String projectIdString
    ) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        int projectId;
        try {
            projectId = Integer.parseInt(projectIdString);
        } catch (NumberFormatException e) {
            return;
        }

        portfolioUserService.setCurrentProject(id,projectId);
        return;
    }
}
