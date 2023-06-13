package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.dtos.filtering.GameFilter;
import ar.edu.itba.paw.dtos.filtering.GameFilterBuilder;
import ar.edu.itba.paw.dtos.filtering.ReviewFilter;
import ar.edu.itba.paw.dtos.filtering.ReviewFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.ordering.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.*;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.datacontainers.FilteredList;
import ar.edu.itba.paw.webapp.controller.helpers.PaginationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.QueryHelper;
import ar.edu.itba.paw.webapp.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.webapp.forms.EditReviewForm;
import ar.edu.itba.paw.webapp.forms.SubmitReviewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReviewController{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);
    private final GameService gameService;
    private final ReviewService reviewService;
    private final UserService userService;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;
    private static final int MAX_SEARCH_RESULTS = 6;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, UserService userService) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.userService = userService;
    }
    @RequestMapping(value = "/review/submit/search")
    public ModelAndView searchGames(@RequestParam(value = "searchquery", defaultValue = "") String searchquery,
                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
                                    @RequestParam(value = "pagesize", defaultValue = "6") Integer pageSize) {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        ModelAndView mav = new ModelAndView("/review/submit-review-search");

        if(pageSize == null || pageSize < 1) {
            pageSize = MAX_SEARCH_RESULTS;
        }

        if(searchquery.isEmpty()){
            List<Game> list = gameService.getRecommendationsOfGamesForUser(loggedUser.getId());
            mav.addObject("games", list.subList(0, Math.min(list.size(), MAX_SEARCH_RESULTS)));
        } else {
            GameFilter filter = new GameFilterBuilder().withGameContent(searchquery).build();
            Paginated<Game> games = gameService.searchGamesNotReviewedByUser(
                    Page.with(page, pageSize),
                    searchquery,
                    new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME),
                    loggedUser.getId()
            );
            PaginationHelper.paginate(mav,games);
            mav.addObject("games", games.getList());
        }
        mav.addObject("searchField", searchquery);

        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
        queriesToKeepAtPageChange.add(new Pair<>("searchquery", searchquery));
        queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));

        return mav;
    }

    @RequestMapping(value = "/review/submit/{gameId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@PathVariable(value = "gameId") Long gameId,
                                         @ModelAttribute("reviewForm") final SubmitReviewForm form) {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        ModelAndView mav = new ModelAndView("/review/submit-review");
        if(gameId == 0){ //If gameId not specified
            return new ModelAndView("redirect:/review/submit/search");
        }
        Optional<Game> reviewedGame = gameService.getGameById(gameId);
        if (!reviewedGame.isPresent()) {
            throw new ObjectNotFoundException("game.notfound");
        }

        mav.addObject("edit", false);
        mav.addObject("game", reviewedGame.get());
        mav.addObject("selectedGameId", gameId);
        mav.addObject("platforms", Platform.values());
        mav.addObject("difficulties", Difficulty.values());
        mav.addObject("units", GamelengthUnit.values());
        return mav;
    }
    @RequestMapping(value = "/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm() {
        return new ModelAndView("redirect:/review/submit/search");
    }

    @RequestMapping(value = "/review/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView reviewDetails(
            @PathVariable(value = "id") Long reviewId,
            @RequestParam(value = "created", required = false) Boolean created
    ) {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        Optional<Review> review = reviewService.getReviewById(reviewId, loggedUser != null ? loggedUser.getId() : null);
        if (!review.isPresent()) {
            throw new ObjectNotFoundException("review.notfound");
        }
        Collection<? extends GrantedAuthority> roles = AuthenticationHelper.getAuthorities();
        ModelAndView mav = new ModelAndView("/review/review-details");
        mav.addObject("review", review.get());
        mav.addObject("created", created != null && created);
        mav.addObject("game", review.get().getReviewedGame());
        mav.addObject("isModerated", roles.contains(new SimpleGrantedAuthority("ROLE_MODERATOR")));
        mav.addObject("isOwner", loggedUser != null && Objects.equals(loggedUser.getId(), review.get().getAuthor().getId()));
        return mav;
    }

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
        User author = AuthenticationHelper.getLoggedUser(userService);
        createdReview = reviewService.createReview(
                form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                author.getId(),
                gameId,
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return new ModelAndView("redirect:/review/" + createdReview.getId() + "?created=true");
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView reviewList(
            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
            @RequestParam(value = "f-tpl", defaultValue = "") String timePlayedFilter,
            @RequestParam(value = "f-prf", defaultValue = "") List<Integer> preferencesFilter,
            @RequestParam(value = "f-plt", defaultValue = "") List<Integer> platformsFilter,
            @RequestParam(value = "f-dif", defaultValue = "") List<Integer> difficultyFilter,
            @RequestParam(value = "f-cpt", defaultValue = "") Boolean completedFilter,
            @RequestParam(value = "f-rpl", defaultValue = "") Boolean replayableFilter,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "search", defaultValue = "") String search
    ) {
        final ModelAndView mav = new ModelAndView("review/review-list");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        List<Genre> allGenres = Arrays.asList(Genre.values());

        if(pageSize == null || pageSize < 1) {
            pageSize = PAGE_SIZE;
        }

        double minTimePlayed = 0;
        try {
            minTimePlayed = Double.parseDouble(timePlayedFilter);
        } catch (Exception ignored) {}

        ReviewFilter searchFilter = new ReviewFilterBuilder()
                .withGameGenres(genresFilter)
                .withPlatforms(
                        platformsFilter.stream()
                        .map(Platform::getById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
                )
                .withDifficulties(
                        difficultyFilter.stream()
                        .map(Difficulty::getById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList())
                )
                .withCompleted((completedFilter != null && completedFilter) ? true : null)
                .withReplayable((replayableFilter!=null && replayableFilter) ? true : null)
                .withAuthorGenres(preferencesFilter)
                .withReviewContent(search)
                .withMinTimePlayed(minTimePlayed)
                .build();


        Paginated<Review> reviewPaginated = reviewService.searchReviews(
                Page.with(page != null ? page : INITIAL_PAGE, pageSize),
                searchFilter,
                new Ordering<>(OrderDirection.fromValue(orderDirection), ReviewOrderCriteria.fromValue(orderCriteria)),
                loggedUser != null ? loggedUser.getId() : null
        );

        PaginationHelper.paginate(mav,reviewPaginated);

        mav.addObject("reviews", reviewPaginated.getList());
        mav.addObject("totalReviews", reviewPaginated.getTotalPages());
        mav.addObject("orderCriteria", ReviewOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("searchField", search);
        mav.addObject("genresFilter", new FilteredList<>(allGenres, (genre) -> genresFilter.contains(genre.getId())));
        mav.addObject("preferencesFilter", new FilteredList<>(allGenres, (preference) -> preferencesFilter.contains(preference.getId())));
        mav.addObject("platformsFilter", new FilteredList<>(Arrays.asList(Platform.values()), (platform) -> platformsFilter.contains(platform.getId())));
        mav.addObject("difficultiesFilter", new FilteredList<>(Arrays.asList(Difficulty.values()), (difficulty) -> difficultyFilter.contains(difficulty.getId())));
        mav.addObject("completedFilter", completedFilter);
        mav.addObject("replayableFilter", replayableFilter);
        mav.addObject("selectedOrderDirection", OrderDirection.fromValue(orderDirection));
        mav.addObject("selectedOrderCriteria", ReviewOrderCriteria.fromValue(orderCriteria));
        mav.addObject("minTimePlayed", minTimePlayed);
        if(orderCriteria== 0 && orderDirection == 0 && genresFilter.isEmpty() && timePlayedFilter.isEmpty() && preferencesFilter.isEmpty() && platformsFilter.isEmpty() && difficultyFilter.isEmpty() && completedFilter == null && replayableFilter == null) {
            mav.addObject("showResetFiltersButton", false);
        } else {
            mav.addObject("showResetFiltersButton", true);
        }

        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
        queriesToKeepAtPageChange.add(Pair.of("o-crit", orderCriteria));
        queriesToKeepAtPageChange.add(Pair.of("o-dir", orderDirection));
        queriesToKeepAtPageChange.add(Pair.of("pageSize", pageSize));
        queriesToKeepAtPageChange.add(Pair.of("search", search));
        queriesToKeepAtPageChange.addAll(genresFilter.stream().map((value) -> Pair.of("f-gen", (Object)value)).collect(Collectors.toList()));
        queriesToKeepAtPageChange.addAll(preferencesFilter.stream().map((value) -> Pair.of("f-prf", (Object)value)).collect(Collectors.toList()));
        queriesToKeepAtPageChange.addAll(platformsFilter.stream().map((value) -> Pair.of("f-plt", (Object)value)).collect(Collectors.toList()));
        queriesToKeepAtPageChange.addAll(difficultyFilter.stream().map((value) -> Pair.of("f-dif", (Object)value)).collect(Collectors.toList()));
        if(completedFilter!=null)
            queriesToKeepAtPageChange.add(Pair.of("f-cpt", completedFilter));
        if(replayableFilter!=null)
            queriesToKeepAtPageChange.add(Pair.of("f-rpl", replayableFilter));
        queriesToKeepAtPageChange.add(Pair.of("f-tpl", timePlayedFilter));

        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));

        List<Pair<String, Object>> queriesToKeepAtRemoveFilters = new ArrayList<>();

        mav.addObject("queriesToKeepAtRemoveFilters", QueryHelper.toQueryString(queriesToKeepAtRemoveFilters));

        if(loggedUser == null || !loggedUser.hasPreferencesSet()) {
            mav.addObject("showFavoritesShortcut", false);
        } else {
            mav.addObject("showFavoritesShortcut", true);
            mav.addObject("userPreferences", loggedUser.getPreferences().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        return mav;
    }

    @RequestMapping(value = "/review/delete/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView deleteReview(@PathVariable(value = "id") Long id) {
        long gameId = reviewService.getReviewById(id, null).orElseThrow(() -> new ObjectNotFoundException("review.notfound"))
                .getReviewedGame().getId();
        boolean deleted = reviewService.deleteReviewById(id);
        if (!deleted) {
            return reviewDetails(id, false);
        }
        return new ModelAndView("redirect:/game/" + gameId);
    }

    @RequestMapping(value = "/review/feedback/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView updateReviewFeedback(@PathVariable(value = "id") Long id,
                                   @RequestParam(value = "feedback") String feedback,
                                   @RequestParam(value = "url", defaultValue = "/") String url){
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        FeedbackType fb;
        try {
            fb = FeedbackType.valueOf(feedback);
        } catch (IllegalArgumentException e){
            throw new ObjectNotFoundException();
        }
        reviewService.updateOrCreateReviewFeedback(id, loggedUser.getId(), fb);
        return new ModelAndView("redirect:" + url);
    }

    @RequestMapping(value = "/review/{id:\\d+}/edit", method = RequestMethod.GET)
    public ModelAndView editReviewForm(@PathVariable("id") Long reviewId, @ModelAttribute("reviewForm") EditReviewForm form) {
        User user = AuthenticationHelper.getLoggedUser(userService);
        Review review = reviewService.getReviewById(reviewId, user.getId()).orElseThrow(ObjectNotFoundException::new);
        form.fromReview(review);
        ModelAndView mav = new ModelAndView("review/submit-review");
        mav.addObject("edit", true);
        mav.addObject("game", review.getReviewedGame());
        mav.addObject("id", reviewId);
        mav.addObject("platforms", Platform.values());
        mav.addObject("difficulties", Difficulty.values());
        mav.addObject("units", GamelengthUnit.values());
        return mav;
    }

    @RequestMapping(value = "/review/{id:\\d+}/edit", method = RequestMethod.POST)
    public ModelAndView editReview(@PathVariable("id") Long reviewId, @Valid @ModelAttribute("reviewForm") EditReviewForm form, final BindingResult errors) {
        if (errors.hasErrors()) {
            return editReviewForm(reviewId, form);
        }
        Review update = reviewService.updateReview(reviewId, form.getReviewTitle(), form.getReviewContent(), form.getReviewRating(), form.getDifficultyEnum(), form.getGameLengthSeconds(), form.getPlatformEnum(),  form.getReplayability(),  form.getReplayability());
        return new ModelAndView("redirect:/review/" + update.getId());
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView reviewExists(ReviewAlreadyExistsException exception) {
        User user = AuthenticationHelper.getLoggedUser(userService);
        Review review = reviewService.getReviewOfUserForGame(user.getId(), exception.getReviewedGame().getId()).orElseThrow(ObjectNotFoundException::new);
        return reviewDetails(review.getId(), false);
    }
}
