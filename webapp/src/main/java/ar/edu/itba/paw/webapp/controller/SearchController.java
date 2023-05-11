package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.GameFilter;
import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.builders.GameFilterBuilder;
import ar.edu.itba.paw.dtos.builders.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
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
                    new ArrayList<>()).addObject("reviews", new ArrayList<>()).addObject("search", search);        }
        Paginated<User> users = userService.getSearchedUsers(1, 5, search);
        GameFilterBuilder gameFilterBuilder = new GameFilterBuilder().withGameContent(search);
        Paginated<Game> games = gameService.getAllGames(Page.with(1, 5),
                gameFilterBuilder.build(),
                new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME));
        ReviewFilterBuilder reviewFilterBuilder = new ReviewFilterBuilder().withReviewContent(search);
        Paginated<Review> reviews = reviewService.getAllReviews(Page.with(1, 5),
                reviewFilterBuilder.build(),
                new Ordering<>(OrderDirection.DESCENDING, ReviewOrderCriteria.REVIEW_DATE),
                AuthenticationHelper.getLoggedUser(userService));
        return new ModelAndView("search/search").addObject("users", users.getList()).addObject("games",
                games.getList()).addObject("reviews", reviews.getList()).addObject("search", search);
    }

/*
    private UserService us;
    @Autowired
    public HelloWorldController(UserService us) {
        this.us = us;
    }

    @RequestMapping("/")
    public ModelAndView helloWorld(){
        final ModelAndView mav =  new ModelAndView("helloworld/index");

        return mav;
    }
    @RequestMapping(value="/register",method = RequestMethod.GET)
    public ModelAndView registerForm(){
        return new ModelAndView("helloworld/register");
    }
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public ModelAndView register(@RequestParam(value = "email",required = true) final String email,
                                 @RequestParam(value = "password",required = true) final String password){
        final User user = us.createUser(email,password);

        final ModelAndView mav= new ModelAndView("helloworld/index");
        mav.addObject("user",user);
        return mav;
    }

    @RequestMapping("/{id:\\d+}")
    public ModelAndView profile(@PathVariable("id") final Long userId){
        ModelAndView mav= new ModelAndView("helloworld/profile");
        final User user = us.getUserById(userId).get();
        mav.addObject("user",user);
        return mav;
    }*/
}
