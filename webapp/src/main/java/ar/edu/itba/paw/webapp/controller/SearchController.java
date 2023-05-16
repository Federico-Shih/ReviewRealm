package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.dtos.searching.GameSearchFilterBuilder;
import ar.edu.itba.paw.dtos.searching.ReviewSearchFilterBuilder;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

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

    @RequestMapping("/search")
    public ModelAndView searchForm(@RequestParam(value = "search", required = false) final String search) {
        if (search == null || search.isEmpty()) {
            return new ModelAndView("search/search").addObject("users", new ArrayList<>()).addObject("games",
                    new ArrayList<>()).addObject("reviews", new ArrayList<>()).addObject("search", search);
        }
        Paginated<User> users = userService.getSearchedUsers(1, MAX_RESULTS, search);
        GameSearchFilterBuilder gameSearchFilterBuilder = new GameSearchFilterBuilder().withSearch(search);
        Paginated<Game> games = gameService.searchGames(Page.with(1, MAX_RESULTS),
                gameSearchFilterBuilder.build(),
                new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME));
        ReviewSearchFilterBuilder reviewSearchFilterBuilder = new ReviewSearchFilterBuilder().withSearch(search);
        Paginated<Review> reviews = reviewService.searchReviews(Page.with(1, MAX_RESULTS),
                reviewSearchFilterBuilder.build(),
                new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_DATE),
                AuthenticationHelper.getLoggedUser(userService));
        return new ModelAndView("search/search").addObject("users", users.getList()).addObject("games",
                games.getList()).addObject("reviews", reviews.getList()).addObject("search", search);
    }
}
