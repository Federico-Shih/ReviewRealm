package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.PaginationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.QueryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    private final UserService userService;
    private final GameService gameService;
    private final ReviewService reviewService;

    private final static int MAX_RESULTS = 6;

    @Autowired
    public SearchController(UserService userService, GameService gameService, ReviewService reviewService) {
        this.userService = userService;
        this.gameService = gameService;
        this.reviewService = reviewService;
    }

//    @RequestMapping("/search")
//    public ModelAndView searchForm(@RequestParam(value = "search", required = false) final String search) {
//        if (search == null || search.isEmpty()) {
//            return new ModelAndView("search/search").addObject("users", new ArrayList<>()).addObject("games",
//                    new ArrayList<>()).addObject("reviews", new ArrayList<>()).addObject("search", search);
//        }
//        Paginated<User> users = userService.searchUsers(
//                Page.with(1, MAX_RESULTS),
//                search,
//                new Ordering<>(OrderDirection.DESCENDING, UserOrderCriteria.LEVEL)
//        );
//        GameFilter gameSearchFilter = new GameFilterBuilder().withGameContent(search).withSuggestion(false).build();
//        Paginated<Game> games = gameService.searchGames(Page.with(1, MAX_RESULTS),
//                gameSearchFilter,
//                new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME));
//
//        ReviewFilter reviewFilter = new ReviewFilterBuilder().withReviewContent(search).build();
//
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//
//        Paginated<Review> reviews = reviewService.searchReviews(Page.with(1, MAX_RESULTS),
//                reviewFilter,
//                new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_DATE),
//                loggedUser == null ? null : loggedUser.getId());
//        return new ModelAndView("search/search").addObject("users", users.getList()).addObject("games",
//                games.getList()).addObject("reviews", reviews.getList()).addObject("search", search);
//    }
//
//    @RequestMapping("/community")
//    public ModelAndView community(
//            @RequestParam(value = "search", defaultValue = "") final String search,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection
//    ) {
//        ModelAndView mav = new ModelAndView("search/community");
//        mav.addObject("userSearch", search);
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        Ordering<UserOrderCriteria> ordering = new Ordering<>(OrderDirection.fromValue(orderDirection), UserOrderCriteria.fromValue(orderCriteria));
//
//        if(!search.isEmpty()) {
//            if (pageSize == null || pageSize < 1) {
//                pageSize = MAX_RESULTS;
//            }
//            Paginated<User> users = userService.searchUsers(Page.with(page, pageSize), search, ordering);
//            PaginationHelper.paginate(mav,users);
//            mav.addObject("searchedUsers", users);
//        }
//
//        boolean userSetPreferences = false;
//        boolean userHasReviewedAnything = false;
//        List<User> samePreferencesUsers = new ArrayList<>();
//        List<User> sameGamesUsers = new ArrayList<>();
//        if(loggedUser != null) {
//            userSetPreferences = loggedUser.hasPreferencesSet();
//            userHasReviewedAnything = userService.hasUserReviewedAnything(loggedUser.getId());
//            if(userSetPreferences) {
//                samePreferencesUsers = userService.getUsersWithSamePreferences(Page.with(1, MAX_RESULTS), loggedUser.getId(), ordering).getList();
//            }
//            if(userHasReviewedAnything) {
//                sameGamesUsers = userService.getUsersWhoReviewedSameGames(Page.with(1, MAX_RESULTS), loggedUser.getId(), ordering).getList();
//            }
//        }
//
//        mav.addObject("samePreferencesUsers", samePreferencesUsers);
//        mav.addObject("sameGamesUsers", sameGamesUsers);
//        mav.addObject("userSetPreferences", userSetPreferences);
//        mav.addObject("userHasReviewedAnything", userHasReviewedAnything);
//        mav.addObject("isLoggedIn", loggedUser!=null);
//
//        Paginated<User> defaultUsers = userService.getOtherUsers(Page.with(1, MAX_RESULTS), loggedUser!=null? loggedUser.getId() : null, ordering);
//        mav.addObject("defaultUsers", defaultUsers);
//
//        mav.addObject("orderCriteria", orderCriteria);
//        mav.addObject("orderDirection", orderDirection);
//        mav.addObject("criteriaOptions", UserOrderCriteria.values());
//        mav.addObject("directionOptions", OrderDirection.values());
//
//
//        if(!search.isEmpty()) {
//            List<Pair<String, Object>> queriesToKeep = new ArrayList<>();
//            queriesToKeep.add(Pair.of("pageSize", pageSize));
//            queriesToKeep.add(Pair.of("search", search));
//            mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeep));
//        }
//        return mav;
//    }
}
