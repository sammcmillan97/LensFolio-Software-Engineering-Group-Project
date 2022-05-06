package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.portfolio.model.User;
import nz.ac.canterbury.seng302.portfolio.model.UserListResponse;
import nz.ac.canterbury.seng302.portfolio.service.PortfolioUserService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

@Controller
public class UserListController {

    @Autowired
    private UserAccountClientService userAccountClientService;

    @Autowired
    private PortfolioUserService portfolioUserService;

    /**
     * Redirects to the first page of the user list.
     * @param principal The authentication state of the user
     * @return The mapping to the html for the first page of the list of users.
     */
    @GetMapping("/userList")
    public String userList(@AuthenticationPrincipal AuthState principal) {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        return "redirect:/userList/1?sortType=" + portfolioUserService.getUserListSortType(id);
    }


    /**
     * Gets the mapping to a page of the list of users html and renders it
     * @param principal The authentication state of the user
     * @param model The model of the html page for the list of users
     * @param page The exact page of the user list we are on. Starts at 1 and increases from there.
     * @return The mapping to the list of users html page.
     */
    @GetMapping("/userList/{page}")
    public String userListSortedPage(@AuthenticationPrincipal AuthState principal,
                               Model model,
                               @PathVariable("page") String page,
                               @RequestParam(name = "sortType") String sortType)
                               {
        int id = Integer.parseInt(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));
        User user = userAccountClientService.getUserAccountById(id);
        model.addAttribute("user", user);
        if (!goodPage(page)) {
            return "redirect:/userList";
        }
        if (!goodSortType(sortType)) {
            return "redirect:/userList/" + page + "?sortType=" + portfolioUserService.getUserListSortType(id);
        }
        int pageInt = Integer.parseInt(page);
        portfolioUserService.setUserListSortType(id, sortType);
        UserListResponse response = userAccountClientService.getPaginatedUsers(10 * pageInt - 10, 10, sortType);
        Iterable<User> users = response.getUsers();
        int maxPage = (response.getResultSetSize() - 1) / 10 + 1;
        if (maxPage == 0) { // If no users are present, one empty page should still display (although this should never happen)
            maxPage = 1;
        }
        if (pageInt > maxPage) {
            return "redirect:/userList/" + maxPage + "?sortType=" + sortType;
        }
        model.addAttribute("users", users);
        model.addAttribute("firstPage", 1);
        model.addAttribute("previousPage", pageInt == 1 ? pageInt : pageInt - 1);
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("nextPage", pageInt == maxPage ? pageInt : pageInt + 1);
        model.addAttribute("lastPage", maxPage);
        model.addAttribute("sortType", sortType);
        return "userList";
    }

    /**
     * Checks whether a string is a valid positive integer.
     * This is the same as checking if it corresponds to a valid page number.
     * This will still accept values that are too big like 9999999999999999999 - these are filtered out at a later step.
     * @param page A string representing a page number
     * @return Whether the provided string is a valid page number
     */
    public boolean goodPage(String page) {
        try {
            return Integer.parseInt(page) >= 1;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether a string is a valid sort type.
     * This is only if it ends in 'A' or 'D' (Ascending or descending)
     * and if the rest of the string corresponds to a column name.
     * @return Whether the provided string is a valid sort type
     */
    public boolean goodSortType(String sortType) {
        HashSet<String> goodSortTypes = new HashSet<>();
        goodSortTypes.add("nameA");
        goodSortTypes.add("nameD");
        goodSortTypes.add("usernameA");
        goodSortTypes.add("usernameD");
        goodSortTypes.add("aliasA");
        goodSortTypes.add("aliasD");
        goodSortTypes.add("rolesA");
        goodSortTypes.add("rolesD");
        return goodSortTypes.contains(sortType);
    }

    @PostMapping("/removeRole/{userId}")
    public String removeRole(@AuthenticationPrincipal AuthState principal,
                                       @PathVariable("userId") int userId,
                                       @RequestParam(name="role") UserRole role,
                                       Model model) {
        UserRoleChangeResponse response = userAccountClientService.addRole(userId, role);
        model.addAttribute("message", response.getMessage());
        return "redirect:/userList";
    }

    @PostMapping("/addRole/{userId}")
    public String addRole(@AuthenticationPrincipal AuthState principal,
                                       @PathVariable("userId") int userId,
                                       @RequestParam(name="role") UserRole role,
                                       Model model) {
        UserRoleChangeResponse response = userAccountClientService.removeRole(userId, role);
        model.addAttribute("message", response.getMessage());
        return "redirect:/userList";
    }

}
