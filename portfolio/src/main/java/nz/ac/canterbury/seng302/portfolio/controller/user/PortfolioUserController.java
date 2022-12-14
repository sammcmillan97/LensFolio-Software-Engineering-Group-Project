package nz.ac.canterbury.seng302.portfolio.controller.user;


import nz.ac.canterbury.seng302.portfolio.service.user.PortfolioUserService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PortfolioUserController {

    @Autowired
    private PortfolioUserService portfolioUserService;

    @GetMapping("setCurrentProject-{id}")
    public String getCurrentProject(
        @AuthenticationPrincipal AuthState principal,
        @PathVariable("id") String projectIdString
    ){
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        int projectId=1;

        try {
            projectId = Integer.parseInt(projectIdString);
            portfolioUserService.setProject(id,projectId);
            return  "redirect:/projectDetails-" + projectId;
        } catch (NumberFormatException e) {
            return "redirect:/projectDetails-" + projectId;
        }
    }
}
