package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.models.Genre;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Optional;

@Controller
public class ReviewController {
    private final GameService gameService;
    private final ReviewService reviewService;
    private final GenreService genreService;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, GenreService genreService) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.genreService = genreService;
    }

    @RequestMapping(value="/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@RequestParam(value = "gameId", required = false) Long gameId) {
        Optional<Game> reviewedGame = gameService.getGameById(gameId);
        if (!reviewedGame.isPresent()) {
            return new ModelAndView("not-found");
        }
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", reviewedGame.get());
        return mav;
    }

    @RequestMapping(value="/review/submit/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitReview(
            @PathVariable(value = "id") Long gameId,
            @RequestParam(value = "review-title") String title,
            @RequestParam(value = "review-content") String content,
            @RequestParam(value = "review-author") String email,
            @RequestParam(value = "review-rating") Integer rating
            ) {
        Review createdReview = reviewService.createReview(title, content, rating, email, gameId);
        if (createdReview == null) {
            return new ModelAndView("not-found");
        }
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", createdReview.getReviewedGame());
        return mav;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView reviewList(
            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter
            /*,@RequestParam(value = "f-pref", defaultValue = "") List<Integer> preferencesFilter*/
    ) {
        final ModelAndView mav = new ModelAndView("review/review-list");
        ReviewFilter filters = new ReviewFilter(genresFilter, new ArrayList<>(), ReviewOrderCriteria.fromValue(orderCriteria), OrderDirection.fromValue(orderDirection));
        List<Genre> allGenres = genreService.getAllGenres();
        mav.addObject("reviews", reviewService.getAllReviews(filters));
        mav.addObject("orderCriteria", ReviewOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());

        mav.addObject("selectedOrderCriteria", orderCriteria);
        mav.addObject("selectedOrderDirection", orderDirection);
        mav.addObject("selectedGenres", allGenres.stream().filter((g) -> genresFilter.contains(g.getId())).collect(Collectors.toList()));
        mav.addObject("unselectedGenres", allGenres.stream().filter((g) -> !genresFilter.contains(g.getId())).collect(Collectors.toList()));
        /*
        mav.addObject("selectedPreferences", allGenres.stream().filter((g) -> preferencesFilter.contains(g.getId())).collect(Collectors.toList()));
        mav.addObject("unselectedPreferences", allGenres.stream().filter((g) -> !preferencesFilter.contains(g.getId())).collect(Collectors.toList()));
        */
        return mav;
    }

}
