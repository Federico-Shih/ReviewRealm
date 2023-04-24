package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.ReviewFilter;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Difficulty;
import ar.edu.itba.paw.enums.GamelengthUnit;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Platform;
import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.forms.SubmitReviewForm;
import org.javatuples.Pair;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReviewController extends PaginatedController {
    private final GameService gameService;
    private final ReviewService reviewService;
    private final GenreService genreService;
    private final UserService userService;

    private static final int MAX_PAGES_PAGINATION = 6;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, GenreService genreService, UserService userService) {
        super(MAX_PAGES_PAGINATION, INITIAL_PAGE);
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.genreService = genreService;
        this.userService = userService;
    }

    @RequestMapping(value = "/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@RequestParam(value = "gameId", required = false) Long gameId,
                                         @ModelAttribute("reviewForm") final SubmitReviewForm form) {
        Optional<Game> reviewedGame = gameService.getGameById(gameId);
        if (!reviewedGame.isPresent()) {
            return new ModelAndView("static-components/not-found");
        }
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", reviewedGame.get());
        mav.addObject("platforms", Platform.values());
        mav.addObject("difficulties", Difficulty.values());
        mav.addObject("units", GamelengthUnit.values());
        return mav;
    }

    @RequestMapping(value = "/review/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView reviewDetails(@PathVariable(value = "id") Long reviewId) {
        Optional<Review> review = reviewService.getReviewById(reviewId);
        if (!review.isPresent()) {
            return new ModelAndView("static-components/not-found");
        }
        ModelAndView mav = new ModelAndView("/review/review-details");
        mav.addObject("review", review.get());
        mav.addObject("game", review.get().getReviewedGame());
        mav.addObject("reviewExtra", ComputedReviewData.factory(review.get()));
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
        Review createdReview;
        Optional<User> author = AuthenticationHelper.getLoggedUser(userService);
        if(!author.isPresent()) {
            //No debería pasar esto, pero por las dudas
            return new ModelAndView("redirect:/login");
        }
        Optional<Game> reviewedGame = gameService.getGameById(gameId);
        if (!reviewedGame.isPresent()) {
            return new ModelAndView("static-components/not-found");
        }
        createdReview = reviewService.createReview(
                form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                author.get(),
                reviewedGame.get(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return new ModelAndView("redirect:/review/" + createdReview.getId());
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView reviewList(
            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "8") Integer pageSize
            /*,@RequestParam(value = "f-pref", defaultValue = "") List<Long> preferencesFilter*/
    ) {
        final ModelAndView mav = new ModelAndView("review/review-list");
        List<Genre> allGenres = genreService.getAllGenres();
        CalculatedReviewFilter filters = new CalculatedReviewFilter(genresFilter, new ArrayList<>(), ReviewOrderCriteria.fromValue(orderCriteria), OrderDirection.fromValue(orderDirection), allGenres);

        Paginated<Review> reviewPaginated = reviewService.getAllReviews(filters,
                page != null ? page : INITIAL_PAGE,
                pageSize != null ? pageSize : PAGE_SIZE);

        super.paginate(mav, reviewPaginated);

        mav.addObject("reviews", reviewPaginated.getList());
        mav.addObject("totalReviews", reviewPaginated.getTotalPages());
        mav.addObject("orderCriteria", ReviewOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("filters", filters);

        List<Pair<String, Object>> queries = new ArrayList<>();
        queries.add(Pair.with("o-crit", orderCriteria));
        queries.add(Pair.with("o-dir", orderDirection));
        queries.add(Pair.with("pageSize", pageSize));
        queries.addAll(genresFilter.stream().map((value) -> Pair.with("f-gen", (Object)value)).collect(Collectors.toList()));

        mav.addObject("queryString", toQueryString(queries));
        return mav;
    }

    private String toQueryString(List<Pair<String, Object>> entries) {
        StringBuilder str = new StringBuilder();
        str.append("?");
        entries.forEach((pair) -> {
            str.append(pair.getValue0());
            str.append("=");
            str.append(pair.getValue1());
            str.append("&");
        });
        return str.toString();
    }

    public static class ComputedReviewData {
        private final Double gametime;

        private final GamelengthUnit unit;

        public static ComputedReviewData factory(Review review) {
            if (review == null || review.getGameLength() == null) return null;
            return new ComputedReviewData(review);
        }

        private ComputedReviewData(Review review) {
            if (review == null || review.getGameLength() == null)
                throw new IllegalStateException();
            if (review.getGameLength() > GamelengthUnit.HOURS.toSeconds(1.0)) {
                this.gametime = review.getGameLength() / GamelengthUnit.HOURS.toSeconds(1.0);
                this.unit = GamelengthUnit.HOURS;
            } else {
                this.gametime = review.getGameLength() / GamelengthUnit.MINUTES.toSeconds(1.0);
                this.unit = GamelengthUnit.MINUTES;
            }
        }

        public Double getGametime() {
            return Math.round(gametime * 100) / 100.0;
        }

        public GamelengthUnit getUnit() {
            return unit;
        }

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
