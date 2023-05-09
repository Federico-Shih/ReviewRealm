package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.*;
import ar.edu.itba.paw.dtos.builders.GameFilterBuilder;
import ar.edu.itba.paw.dtos.ordering.GameOrderCriteria;
import ar.edu.itba.paw.dtos.ordering.OrderDirection;
import ar.edu.itba.paw.dtos.ordering.Ordering;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.forms.SubmitGameForm;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.datacontainers.CalculatedFilter;
import org.javatuples.Pair;
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

    private static final int MAX_PAGES_PAGINATION = 6;

    private static final int PAGE_SIZE = 10;

    private static final int INITIAL_PAGE = 1;

    @Autowired
    public GameController(GenreService grs, GameService gs, UserService us) {
        super(MAX_PAGES_PAGINATION, INITIAL_PAGE);
        this.grs = grs;
        this.gs = gs;
        this.us = us;
    }

    @RequestMapping("/game/{id:\\d+}")
    public ModelAndView game_details(@PathVariable("id") final Long gameId){
        final ModelAndView mav =  new ModelAndView("games/game-details");
        Optional<Game> game = gs.getGameById(gameId);
        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        if(game.isPresent()){
            mav.addObject("game",game.get());
            GameReviewData reviewData = gs.getReviewsByGameId(gameId,loggedUser);
            //Si esta el juego entonces si o si estan las reviews aunque sean vacias, no hay que chequear
            mav.addObject("gameReviewData", reviewData);

        }else{
            return new ModelAndView("static-components/not-found");
        }
        return mav;
    }

    @RequestMapping(value = "/game/list", method = RequestMethod.GET)
    public ModelAndView gameList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "o-crit", defaultValue = "1") Integer orderCriteria,
            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
            @RequestParam(value = "search", defaultValue = "") String search ){
        final ModelAndView mav = new ModelAndView("games/game-list");

        List<Genre> allGenres = grs.getAllGenres();
        GameFilterBuilder filterBuilder = new GameFilterBuilder()
                .withGameContent(search)
                .withGameGenres(genresFilter);
        GameFilter filter = filterBuilder.getFilter();

        Paginated<Game> games = gs.getAllGames(
                Page.with(page != null ? page: INITIAL_PAGE, PAGE_SIZE),
                filter,
                new Ordering<>(OrderDirection.fromValue(orderDirection), GameOrderCriteria.fromValue(orderCriteria))
        );


        super.paginate(mav,games);

        mav.addObject("games", games.getList());
        mav.addObject("currentPage", page);
        mav.addObject("orderCriteria", GameOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("searchField",search);
        mav.addObject("filters", new CalculatedFilter(genresFilter, new ArrayList<>(), allGenres));
        mav.addObject("selectedOrderDirection", OrderDirection.fromValue(orderDirection));
        mav.addObject("selectedOrderCriteria", GameOrderCriteria.fromValue(orderCriteria));

        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
        queriesToKeepAtPageChange.add(Pair.with("o-crit", orderCriteria));
        queriesToKeepAtPageChange.add(Pair.with("o-dir", orderDirection));
        queriesToKeepAtPageChange.add(Pair.with("search", search));
        queriesToKeepAtPageChange.addAll(genresFilter.stream().map((value) -> Pair.with("f-gen", (Object)value)).collect(Collectors.toList()));

        mav.addObject("queriesToKeepAtPageChange", toQueryString(queriesToKeepAtPageChange));

        List<Pair<String, Object>> queriesToKeepAtRemoveFilters = new ArrayList<>();
        queriesToKeepAtRemoveFilters.add(Pair.with("o-crit", orderCriteria));
        queriesToKeepAtRemoveFilters.add(Pair.with("o-dir", orderDirection));
        queriesToKeepAtRemoveFilters.add(Pair.with("search", search));

        mav.addObject("queriesToKeepAtRemoveFilters", toQueryString(queriesToKeepAtRemoveFilters));
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
            return (game.isPresent())? new ModelAndView("redirect:/game/" + game.get().getId())
                    :
                    new ModelAndView("redirect:/game/list"); //TODO: Hacer un toast de que llegó la sugerencia
        } catch (IOException e) {
            LOGGER.error("Failed to create image: {}", e.getMessage());
            errors.addError(new ObjectError("image", "game.submit.errors.failedimg"));
        } catch (RuntimeException e) {
            LOGGER.error("Unknown error: {}", e.getMessage());
        }
        return createGameForm(gameForm);
    }
}
