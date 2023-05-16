package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.dtos.searching.GameSearchFilter;
import ar.edu.itba.paw.dtos.searching.GameSearchFilterBuilder;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.webapp.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.webapp.forms.SubmitGameForm;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.datacontainers.FilteredList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GameController extends PaginatedController implements QueryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final GenreService grs;
    private final GameService gs;
    private final UserService us;
    private final ReviewService rs;

    private static final int MAX_PAGES_PAGINATION = 6;

    private static final int PAGE_SIZE = 10;

    private static final int INITIAL_PAGE = 1;

    @Autowired
    public GameController(GenreService grs, GameService gs, UserService us, ReviewService rs) {
        super(MAX_PAGES_PAGINATION, INITIAL_PAGE);
        this.grs = grs;
        this.gs = gs;
        this.us = us;
        this.rs = rs;
    }

    @RequestMapping("/game/{id:\\d+}")
    public ModelAndView game_details(
            @PathVariable("id") final Long gameId,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "created", required = false) Boolean created
    ){
        final ModelAndView mav =  new ModelAndView("games/game-details");
        mav.addObject("created", created != null && created);
        Optional<Game> game = gs.getGameById(gameId);
        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        if(pageSize == null || pageSize < 0) {
            pageSize = PAGE_SIZE;
        }
        if(game.isPresent()){
            mav.addObject("game",game.get());
            GameReviewData reviewData = gs.getGameReviewDataByGameId(gameId);
            List<Review> reviews = rs.getReviewsFromGame(Page.with(1, pageSize), gameId, loggedUser).getList();
            mav.addObject("gameReviewData", reviewData);
            mav.addObject("reviews", reviews);
            mav.addObject("currentPageSize", pageSize);
            mav.addObject("defaultPageSize", PAGE_SIZE);
            mav.addObject("discoveryQueue",false);
            mav.addObject("queryString", "?" );
        }else{
            throw new ObjectNotFoundException();
        }
        return mav;
    }

    @RequestMapping(value = "/game/list", method = RequestMethod.GET)
    public ModelAndView gameList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
            @RequestParam(value = "f-rat", defaultValue = "") String ratingFilter,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "created", required = false) Boolean created
    ){
        final ModelAndView mav = new ModelAndView("games/game-list");

        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        List<Genre> allGenres = grs.getAllGenres();

        if(pageSize == null || pageSize < 0) {
            pageSize = PAGE_SIZE;
        }

        GameSearchFilterBuilder searchFilterBuilder = new GameSearchFilterBuilder()
                .withSearch(search)
                .withSuggestion(false)
                .withGenres(genresFilter);
        float minRating = 1f;
        float maxRating = 10f;
        try {
            String [] ratingFilterArray = ratingFilter.split("t");
            minRating = Float.parseFloat(ratingFilterArray[0]);
            maxRating = Float.parseFloat(ratingFilterArray[1]);
            searchFilterBuilder = searchFilterBuilder.withRatingRange(minRating,maxRating);
        } catch (Exception ignored) {}

        GameSearchFilter searchFilter = searchFilterBuilder.build();

        Paginated<Game> games = gs.searchGames(
                Page.with(page != null ? page: INITIAL_PAGE, pageSize),
                searchFilter,
                new Ordering<>(OrderDirection.fromValue(orderDirection), GameOrderCriteria.fromValue(orderCriteria))
        );

        super.paginate(mav,games);
        mav.addObject("created", created != null && created);
        mav.addObject("games", games.getList());
        mav.addObject("currentPage", page);
        mav.addObject("orderCriteria", GameOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("searchField",search);
        mav.addObject("genresFilter", new FilteredList<Genre>(allGenres, (genre) -> genresFilter.contains(genre.getId())));
        mav.addObject("selectedOrderDirection", OrderDirection.fromValue(orderDirection));
        mav.addObject("selectedOrderCriteria", GameOrderCriteria.fromValue(orderCriteria));
        mav.addObject("minRating", minRating);
        mav.addObject("maxRating", maxRating);
        if(orderCriteria == 0 && orderDirection == 0 && genresFilter.isEmpty() && search.isEmpty() && ratingFilter.isEmpty()){
            mav.addObject("showResetFiltersButton", false);
        } else {
            mav.addObject("showResetFiltersButton", true);
        }

        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
        queriesToKeepAtPageChange.add(Pair.of("o-crit", orderCriteria));
        queriesToKeepAtPageChange.add(Pair.of("o-dir", orderDirection));
        queriesToKeepAtPageChange.add(Pair.of("search", search));
        queriesToKeepAtPageChange.add(Pair.of("f-rat", ratingFilter));
        queriesToKeepAtPageChange.addAll(genresFilter.stream().map((value) -> Pair.of("f-gen", (Object)value)).collect(Collectors.toList()));

        mav.addObject("queriesToKeepAtPageChange", toQueryString(queriesToKeepAtPageChange));

        List<Pair<String, Object>> queriesToKeepAtRemoveFilters = new ArrayList<>();

        mav.addObject("queriesToKeepAtRemoveFilters", toQueryString(queriesToKeepAtRemoveFilters));

        if(loggedUser == null || !loggedUser.hasPreferencesSet()) {
            mav.addObject("showFavoritesShortcut", false);
        } else {
            mav.addObject("showFavoritesShortcut", true);
            mav.addObject("userPreferences", loggedUser.getPreferences().stream().map(Genre::getId).collect(Collectors.toList()));
        }
        return mav;
    }


    @RequestMapping(value = "/game/submit", method = RequestMethod.GET)
    public ModelAndView createGameForm(@ModelAttribute("gameForm") SubmitGameForm gameForm) {
        ModelAndView mav = new ModelAndView("games/submit-game");
        mav.addObject("genres", Genre.values());
        return mav;
    }

    @RequestMapping(value = "/game/submit", method = RequestMethod.POST)
    public ModelAndView createGame(@Valid() @ModelAttribute("gameForm") SubmitGameForm gameForm, final BindingResult errors) {
        if (errors.hasErrors()) {
            return createGameForm(gameForm);
        }
        try {
            Optional<Game> game = gs.createGame(gameForm.toSubmitDTO(), AuthenticationHelper.getLoggedUser(us).getId());
            return game.map(value -> new ModelAndView("redirect:/game/" + value.getId() + "?created=true")).orElseGet(() -> new ModelAndView("redirect:/game/list" + "?created=true"));
        } catch (IOException e) {
            LOGGER.error("Failed to create image: {}", e.getMessage());
            errors.addError(new ObjectError("image", "game.submit.errors.failedimg"));
        } catch (RuntimeException e) {
            errors.addError(new ObjectError("image", "game.submit.errors.unknown"));
            return createGameForm(gameForm);
        }
        return createGameForm(gameForm);
    }

    @RequestMapping(value = "/game/submissions/{gameId:\\d+}/accept", method = RequestMethod.POST)
    public ModelAndView acceptSubmission(@PathVariable(value="gameId") Long gameId) {
        LOGGER.info("Accepting suggested game, id: {}", gameId);
        gs.acceptGame(gameId);
        return new ModelAndView("redirect:/game/submissions");
    }

    @RequestMapping(value = "/game/submissions/{gameId:\\d+}/reject", method = RequestMethod.POST)
    public ModelAndView rejectSubmission(@PathVariable(value="gameId") Long gameId) {
        LOGGER.info("Rejecting suggested game, id: {}", gameId);
        gs.rejectGame(gameId);
        return new ModelAndView("redirect:/game/submissions");
    }

    @RequestMapping(value = "/game/submissions", method = RequestMethod.GET)
    public ModelAndView checkSubmissions() {
        ModelAndView mav = new ModelAndView("/games/game-addition");

        GameSearchFilterBuilder searchFilterBuilder = new GameSearchFilterBuilder()
                .withSuggestion(true);
        GameSearchFilter searchFilter = searchFilterBuilder.build();

        Paginated<Game> games = gs.searchGames(
                Page.with(INITIAL_PAGE, PAGE_SIZE),
                searchFilter,
                new Ordering<>(OrderDirection.fromValue(0), GameOrderCriteria.fromValue(1))
        );
        super.paginate(mav,games);
        mav.addObject("suggestedgames", games.getList());
        return mav;
    }
}
