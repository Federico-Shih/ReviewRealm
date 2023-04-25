package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.CalculatedFilter;
import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.Filter;
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
public class ReviewController extends PaginatedController implements QueryController {
    private final GameService gameService;
    private final ReviewService reviewService;
    private final GenreService genreService;
    private final UserService userService;

    private static final int MAX_PAGES_PAGINATION = 6;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;

    private static final int MAX_SEARCH_RESULTS = 5;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, GenreService genreService, UserService userService) {
        super(MAX_PAGES_PAGINATION, INITIAL_PAGE);
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.genreService = genreService;
        this.userService = userService;
    }

    @RequestMapping(value = "/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@RequestParam(value = "gameId", defaultValue = "0", required = false) Long gameId,
                                         @RequestParam(value = "search", defaultValue = "") String search,
                                         @ModelAttribute("reviewForm") final SubmitReviewForm form) {
        ModelAndView mav = new ModelAndView("/review/submit-review");

        if(gameId != 0) {
            Optional<Game> reviewedGame = gameService.getGameById(gameId);
            if (!reviewedGame.isPresent()) {
                return new ModelAndView("static-components/not-found");
            }
            mav.addObject("game", reviewedGame.get());
        }

        if(!search.isEmpty()){
            mav.addObject("searchedGames", gameService.getAllGamesShort(INITIAL_PAGE,MAX_SEARCH_RESULTS, search).getList());
        }else{
            mav.addObject("searchedGames", new ArrayList<Game>());
        }
        mav.addObject("selectedGameId", gameId);
        mav.addObject("searchField", search);
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
            return createReviewForm(gameId, "",form);
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
        CalculatedFilter filters = new CalculatedFilter(genresFilter, new ArrayList<>(), ReviewOrderCriteria.fromValue(orderCriteria),null, OrderDirection.fromValue(orderDirection), allGenres);

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


}
