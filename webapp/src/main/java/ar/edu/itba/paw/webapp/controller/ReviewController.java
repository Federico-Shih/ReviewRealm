package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.webapp.form.SubmitReviewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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

    @RequestMapping(value = "/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@RequestParam(value = "gameId", required = false) Long gameId,
                                         @ModelAttribute("reviewForm") final SubmitReviewForm form) {
        Optional<Game> reviewedGame = gameService.getGameById(gameId);
        if (!reviewedGame.isPresent()) {
            return new ModelAndView("not-found");
        }
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", reviewedGame.get());
        return mav;
    }

    // de no utilizar ModelAttribute, habría que hacer otro mav.addObject("form",form)
    @RequestMapping(value = "/review/submit/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitReview(
            @PathVariable(value = "id") Long gameId,
            @Valid @ModelAttribute("reviewForm") final SubmitReviewForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return createReviewForm(gameId, form);
        }

        Review createdReview = reviewService.createReview(form.getReviewTitle(),
                form.getReviewContent(), form.getReviewRating(), form.getReviewAuthor(), gameId);

        return new ModelAndView("redirect:/game/" + createdReview.getReviewedGame().getId());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView reviewList(
            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter
            /*,@RequestParam(value = "f-pref", defaultValue = "") List<Long> preferencesFilter*/
    ) {
        final ModelAndView mav = new ModelAndView("review/review-list");
        List<Genre> allGenres = genreService.getAllGenres();
        CalculatedReviewFilter filters = new CalculatedReviewFilter(genresFilter, new ArrayList<>(), ReviewOrderCriteria.fromValue(orderCriteria), OrderDirection.fromValue(orderDirection), allGenres);

        mav.addObject("reviews", reviewService.getAllReviews(filters));
        mav.addObject("orderCriteria", ReviewOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("filters", filters);
        return mav;
    }

    public static class CalculatedReviewFilter extends ReviewFilter {
        private final List<Genre> unselectedGenres;
        private final List<Genre> selectedGenres;
        private final List<Genre> unselectedPreferences;
        private final List<Genre> selectedPreferences;

        public CalculatedReviewFilter(
                List<Integer> gameGenresFilter,
                List<Integer> reviewerPreferencesFilter,
                ReviewOrderCriteria reviewOrderCriteria,
                OrderDirection orderDirection,
                List<Genre> allGenres) {
            super(gameGenresFilter, reviewerPreferencesFilter, reviewOrderCriteria, orderDirection);
            this.unselectedGenres = allGenres.stream().filter((g) -> !gameGenresFilter.contains(g.getId())).collect(Collectors.toList());
            this.selectedGenres = allGenres.stream().filter((g) -> gameGenresFilter.contains(g.getId())).collect(Collectors.toList());
            this.unselectedPreferences = allGenres.stream().filter((g) -> !reviewerPreferencesFilter.contains(g.getId())).collect(Collectors.toList());
            this.selectedPreferences = allGenres.stream().filter((g) -> reviewerPreferencesFilter.contains(g.getId())).collect(Collectors.toList());
        }

        public List<Genre> getUnselectedGenres() {
            return unselectedGenres;
        }

        public List<Genre> getSelectedGenres() {
            return selectedGenres;
        }

        public List<Genre> getUnselectedPreferences() {
            return unselectedPreferences;
        }

        public List<Genre> getSelectedPreferences() {
            return selectedPreferences;
        }
    }

}
