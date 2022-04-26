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
     * Redirects to the first page of the user list.
     * @return The mapping to the html for the first page of the list of users.
     */
    @GetMapping("/userList")
    public String userList() {
        return "redirect:/userList/1";
    }

    /**
     * Gets the mapping to a page of the list of users html and renders it
     * @param principal The authentication state of the user
     * @param model The model of the html page for the list of users
     * @param page The exact page of the user list we are on. Starts at 1 and increases from there.
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
        if (maxPage == 0) { // If no users are present, one empty page should still display (although this should never happen)
            maxPage = 1;
        }
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

    /**
     * Checks whether a string is a valid positive integer.
     * This is the same as checking if it corresponds to a valid page number.
     * This will still accept values that are too big like 9999999999999999999 - these are filtered out at a later step.
     * @param page A string representing a page number
     * @return Whether the provided string is a valid page number
     */
    private boolean goodPage(String page) {
        try {
            return Integer.parseInt(page) >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
