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
        String sortType = portfolioUserService.getUserListSortType(id);
        String isAscending = String.valueOf(portfolioUserService.isUserListSortAscending(id));
        return "redirect:/userList/1" + sortingSuffix(sortType, isAscending);
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
                               @RequestParam(name = "sortType", required = false) String sortType,
                               @RequestParam(name = "isAscending", required = false) String isAscending)
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
        // If either sort parameter is wrong, redirect to a page where the wrong sort parameters are replaced by previous values.
        if (!isGoodSortType(sortType) || !isGoodAscending(isAscending)) {
            if (!isGoodSortType(sortType)) {
                sortType = portfolioUserService.getUserListSortType(id);
            }
            if (!isGoodAscending(isAscending)) {
                isAscending = String.valueOf(portfolioUserService.isUserListSortAscending(id));
            }
            return "redirect:/userList/" + page + sortingSuffix(sortType, isAscending);
        }
        int pageInt = Integer.parseInt(page);
        portfolioUserService.setUserListSortType(id, sortType);
        // We can safely parse the boolean as it has already been checked to see if it is either true or false
        boolean sortAscending = Boolean.parseBoolean(isAscending);
        portfolioUserService.setUserListSortAscending(id, sortAscending);
        UserListResponse response = userAccountClientService.getPaginatedUsers(10 * pageInt - 10, 10, sortType, sortAscending);
        Iterable<User> users = response.getUsers();
        int maxPage = (response.getResultSetSize() - 1) / 10 + 1;
        if (maxPage == 0) { // If no users are present, one empty page should still display (although this should never happen)
            maxPage = 1;
        }
        if (pageInt > maxPage) {
            return "redirect:/userList/" + maxPage + sortingSuffix(sortType, isAscending);
        }
        model.addAttribute("users", users);
        model.addAttribute("firstPage", 1);
        model.addAttribute("previousPage", pageInt == 1 ? pageInt : pageInt - 1);
        model.addAttribute("currentPage", pageInt);
        model.addAttribute("nextPage", pageInt == maxPage ? pageInt : pageInt + 1);
        model.addAttribute("lastPage", maxPage);
        model.addAttribute("sortType", sortType);
        model.addAttribute("isAscending", isAscending);
        model.addAttribute("sortingSuffix", sortingSuffix(sortType, isAscending));
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
     * This is only if it corresponds to a column name.
     * @return Whether the provided string is a valid sort type
     */
    public boolean isGoodSortType(String sortType) {
        HashSet<String> goodSortTypes = new HashSet<>();
        goodSortTypes.add("name");
        goodSortTypes.add("username");
        goodSortTypes.add("alias");
        goodSortTypes.add("roles");
        return goodSortTypes.contains(sortType);
    }

    /**
     * Checks whether a string is a valid boolean for deciding whether to sort in ascending or descending order.
     * This is only if it is the string 'true' or 'false'
     * @return Whether the provided string is a valid type for deciding whether to sort in ascending or descending order.
     */
    public boolean isGoodAscending(String isAscending) {
        HashSet<String> goodAscendingTypes = new HashSet<>();
        goodAscendingTypes.add("true");
        goodAscendingTypes.add("false");
        return goodAscendingTypes.contains(isAscending);
    }

    /**
     * Returns a suffix for sorting in the url for the user list page.
     * Is of the form ?sortType=x&isAscending=y, where sortType is x and isAscending is y.
     * @param sortType The sort type for sorting the user list
     * @param isAscending Whether the user list should be sorted in ascending order
     * @return The url suffix for sorting
     */
    public String sortingSuffix(String sortType, String isAscending) {
        return "?sortType=" + sortType + "&isAscending=" + isAscending;
    }

    @PostMapping("/removeRole")
    public String removeRole(@AuthenticationPrincipal AuthState principal,
                                       @RequestParam(name="userId") int userId,
                                       @RequestParam(name="roleType") UserRole role,
                                       Model model) {
        System.out.println("user id: " + userId);
        System.out.println("Role: " + role);
        UserRoleChangeResponse response = userAccountClientService.removeRole(userId, role);
        model.addAttribute("message", response.getMessage());
        return "redirect:/userList";
    }

    @PostMapping("/addRole")
    public String addRole(@AuthenticationPrincipal AuthState principal,
                          @RequestParam(name="userId") int userId,
                          @RequestParam(name="roleType") UserRole role,
                                       Model model) {
        System.out.println("user id: " + userId);
        System.out.println("Role: " + role);
        System.out.println(role.getDescriptorForType());
        UserRoleChangeResponse response = userAccountClientService.addRole(userId, role);
        model.addAttribute("message", response.getMessage());
        return "redirect:/userList";
    }

}
