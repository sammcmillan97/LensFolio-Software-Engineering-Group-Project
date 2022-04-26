package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserListController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    /**
     * Gets the mapping to the list of users page html and renders it
     * @param principal The authentication state of the user
     * @param model The model of the html page for the list of users
     * @return The mapping to the list of users html page.
     */
    @GetMapping("/userList")
    public String userList(@AuthenticationPrincipal AuthState principal,
                           Model model) {
        return "redirect:/userList/1";
    }

    /**
     * Gets the mapping to the list of users page html and renders it
     * @param principal The authentication state of the user
     * @param model The model of the html page for the list of users
     * @return The mapping to the list of users html page.
     */
    @GetMapping("/userList/{page}")
    public String userListPage(@AuthenticationPrincipal AuthState principal,
                               Model model,
                               @PathVariable("page") String page) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        User user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        if (!goodPage(page)) {
            return "redirect:/userList/1";
        }
        int pageInt = Integer.parseInt(page);
        UserListResponse response = userAccountClientService.getPaginatedUsers(10 * pageInt - 10, 10 * pageInt, "nameA");
        Iterable<User> users = response.getUsers();
        int maxPage = (response.getResultSetSize() - 1) / 10 + 1;
        if (pageInt > maxPage) {
            return "redirect:/userList/" + maxPage;
        }
        model.addAttribute("users", users);
        model.addAttribute("firstPage", 1);
        model.addAttribute("previousPage", pageInt == 1 ? pageInt : pageInt - 1);
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("nextPage", pageInt == maxPage ? pageInt : pageInt + 1);
        model.addAttribute("lastPage", maxPage);
        return "userList";
    }

    private boolean goodPage(String page) {
        try {
            return Integer.parseInt(page) >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
