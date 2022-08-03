package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.WebLink;
import nz.ac.canterbury.seng302.portfolio.service.EvidenceService;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * The controller for handling backend of the add evidence page
 */
@Controller
public class AddEvidenceController {

    private static final String ADD_EVIDENCE = "addEvidence";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserAccountClientService userService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    @Autowired
    private EvidenceService evidenceService;

    private static final String TIMEFORMAT = "yyyy-MM-dd";

    /**
     * Display the add evidence page.
     * @param principal Authentication state of client
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return The add evidence page.
     */
    @GetMapping("/addEvidence")
    public String addEvidence(
            @AuthenticationPrincipal AuthState principal,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);

        int userId = userService.getUserId(principal);
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        Project project = projectService.getProjectById(projectId);

        Evidence evidence;

        Date evidenceDate;
        Date currentDate = new Date();

        if(currentDate.after(project.getStartDate()) && currentDate.before(project.getEndDate())) {
            evidenceDate = currentDate;
        } else {
            evidenceDate = project.getStartDate();
        }

        evidence = new Evidence(userId, projectId, "", "", evidenceDate);

        model.addAttribute("evidenceTitle", evidence.getTitle());
        model.addAttribute("evidenceDescription", evidence.getDescription());
        model.addAttribute("evidenceDate", Project.dateToString(evidence.getDate(), TIMEFORMAT));
        model.addAttribute("minEvidenceDate", Project.dateToString(project.getStartDate(), TIMEFORMAT));
        model.addAttribute("maxEvidenceDate", Project.dateToString(project.getEndDate(), TIMEFORMAT));
        return ADD_EVIDENCE;
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
    @PostMapping("/addEvidence")
    public String saveEvidence(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="evidenceTitle") String title,
            @RequestParam(name="evidenceDescription") String description,
            @RequestParam(name="evidenceDate") String dateString,
            Model model
    ) {
        User user = userService.getUserAccountByPrincipal(principal);
        model.addAttribute("user", user);
        Date date;
        try {
            date = new SimpleDateFormat(TIMEFORMAT).parse(dateString);
        } catch (ParseException exception) {
            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }
        int userId = userService.getUserId(principal);
        int projectId = portfolioUserService.getUserById(userId).getCurrentProject();
        Evidence evidence = new Evidence(userId, projectId, title, description, date);
        evidence.addWebLink(new WebLink("http://localhost:9000/portfolio", "Portfolio Link"));
        evidence.addWebLink(new WebLink("http://www.reallylong.link/rll/OkK/1Kthfkw0D8Hu3F2BcuEHCFvzs0RGTBg1LjiG6POqiFGPRb3PcYobzxLTS5s6B0fnvChoitTOYaBI8oXgmphwasBvlSL6NMR3Da4n3ZiFIr_MmXx6Q98ySulsS7/oHQUYIYjsqk8E_f2ZzizmLkdkH0xbZLAOoJ2_q5HvZFlzU75U3n5LswVt06BVwTBzU19jvC5loNAgaUWzfOFq0hpzQO9zEA_/JTozbzBnQkqVQM9qAbSBveZ7LSuqZmRwW/tg8o9Rr8JYeyjXF8AFg8yUhDaeReQsDe1SrcpuID8S9sTszUPODb3_671p9ekcpQMHEcgIWPVZi3vaPRXXMegoY7BXT6VWiYw8wJg9iBtZSyyPAs28wDCejEdTxf_qO6UKBYJa8hDxbbioTMzmsPhV4BKxZhX5M7g_n5/9el5gp8CFCMeHJHA7E2FDiFe62BlcNjSjiVtfNE2ei2jyvAqIlaRQahavfmadf/1Z2Bqy6R6uhEQS3baFe/3LaZxL5z5YRbU8uTWT17BoC/1KOH/V3jvVXi7K5frF/wxUhDA1OlKbiVMNdHiuBy6NxV9MFAOvlS7KsjX3YEEAw67lo5bKJZEJZOIJ3cGkSJP59meQc4HYIoOjo0q3y_6Cm44zuzaOLDAyeKj2EvLHHxMsImeqWb6pUXTLrta22sXY0N2Xq1fRU9t1eIliQQweLF8BXBW1FPt8cIO3YKhCT10ZjHi_Qnp52/AM9nPQet_g5QACA3OmTpc4H9anYIejq65jHgDwPRsY_3/7hucjKqMrf_AKSlyFsv7vNK7VsS95gowfBan8UwEXscK2Nel4VRQ5CHGzwpsNE6gmIUtCnKa0_P2Pp8tQByN_VfBGBzQClHHs4KMRvGP3WmzGGH12vI7hIli6yM5IvaWact7KOC7oBMKzLM0PMRlpM3WarArvavubuOp5WJnkynurstGnA4pNOQqlzddMAxAiTNnm1RTfjn5DZjR940m2mTJ31OZ7mhf8TMCplHehprBHwNq/gvfEk07SViSNKKKrH6/vc/ZMAhUM249lxMwvU3qOK2ZV/6wJJeIPIxmn1XDuDMtPrTTI7gyg8n_MTt17Rn1IJFToziBYc1j6zzz3DUL/KeJaHcxZ88vdeAo1kSjF9Dh1mjYKlKytfUcEKckRtzSOg1OwhCrIuivDGm9oD8Oc_ql5Peq0YTK6QLUUNTwmH3INSosK1vVRaH6f_tBSjmRFKadOWpFcBEEGB41SwyYsIxWloDZYFGleWLl7lT6lNNGLOppIJIkWO9w98VSTg_pOFzOCN7hhCKssgVCUNWfW2rqadg_6Y1EDYceewWSNnBNSeMfmNgUtrWyBHioCzFU5Nvlkn1avoVFcOinetXXZc1qO2O910fTNcoV8M51IVDAhYZ_x9dtj/yZp/9TzargtolYTmAyDetarHHM_te6bQdAlw6ogL71YDoHbQf0rJOhxuzJmuShNGl05Lv5wY93yZr7XoKOCOvYVnaGTNGdLyRytWF08mfjClogiYjHZwJqzDsBlWykoVwpHFrD0rV_BVPJ3eJNeXkSU_stc5zbgEM6zcV1X7kGe51u35TcMM8nS2PKQULFfxvqt0MXZ8OmceIkuhirTlxPZVZa__2YJDufe/JRbX29zcxDKPj2DQodQ8lhogvodJ4LZ3P8kYNqvVAX3ziPEWheBFW0765qkkd3rRpEEyR_7eJeZ98QM/ajEygAk49m9pLrTM6qwiCd9Jscd4hEmLMIhrj/7N2wPnTBB37M_NyxGE4rgOVnoc70qKNlpFjaH7yeSmn8A0ao12z3x1Gn4Q9l6QKph9deiNOG0rrAKiWje3mzv7bhvLKzAp/HjEHfb1aKZj2BLjHcwJ/ooyb4j0F7uETel5G3v3PfyQl5Mmjhe9IT_mi9krQtTWSj/_cQIKcuojD2Z_cQEWktNPU7NGXAfhezdwnR9bb7aVbPM6YD", "Portfolio Link"));
        evidence.addWebLink(new WebLink("http://localhost:9000/portfolio"));
        evidence.addWebLink(new WebLink("http://localhost:9000/portfolio", "Portfolio Link"));
        try {
            evidenceService.saveEvidence(evidence);
        } catch (IllegalArgumentException exception) {
            return ADD_EVIDENCE; // Fail silently as client has responsibility for error checking
        }
        return "redirect:/portfolio";
    }

    /**
     * Save one web link. Redirects to portfolio page with no message if evidence does not exist.
     * If correctly saved, redirects to projects page.
     * @param principal Authentication state of client
     * @param evidenceId Id of the evidence to add the link to
     * @param webLink The link string to be added to evidence of id=evidenceId
     * @param model Parameters sent to thymeleaf template to be rendered into HTML
     * @return Redirect to portfolio page.
     */
    @PostMapping("/addWebLink")
    public String addWebLink(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(name="projectId") String evidenceId,
            @RequestParam(name="webLink") String webLink,
            @RequestParam(name="webLinkName") String webLinkName,
            Model model
    ) {
        int id = Integer.parseInt(evidenceId);
        try {
            WebLink webLink1;
            if (webLinkName.isEmpty()) {
                webLink1 = new WebLink(webLink);
            } else {
                webLink1 = new WebLink(webLink, webLinkName);
            }
            evidenceService.saveWebLink(id, webLink1);
        } catch (NoSuchElementException e) {
            return "redirect:/portfolio";
        }
        return "redirect:/portfolio";
    }

}

