package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.CalculatedFilter;
import ar.edu.itba.paw.dtos.GameOrderCriteria;
import ar.edu.itba.paw.dtos.OrderDirection;
import ar.edu.itba.paw.dtos.ReviewOrderCriteria;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class GameController extends PaginatedController implements QueryController {

    private GenreService grs;
    private GameService gs;

    private static final int MAX_PAGES_PAGINATION = 6;

    private static final int PAGE_SIZE = 10;

    private static final int INITIAL_PAGE = 1;

    @Autowired
    public GameController(GenreService grs, GameService gs) {
        super(MAX_PAGES_PAGINATION, INITIAL_PAGE);
        this.grs = grs;
        this.gs = gs;
    }

    @RequestMapping("/game/{id:\\d+}")
    public ModelAndView game_details(@PathVariable("id") final Long gameId){
        final ModelAndView mav =  new ModelAndView("games/game-details");
        Optional<Game> game = gs.getGameById(gameId);
        if(game.isPresent()){
            mav.addObject("game",game.get());
            GameReviewData reviewData = gs.getReviewsByGameId(gameId);
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
        CalculatedFilter filters = new CalculatedFilter(genresFilter, new ArrayList<>(),null, GameOrderCriteria.fromValue(orderCriteria), OrderDirection.fromValue(orderDirection), allGenres);

        Paginated<GameData> games = gs.getAllGames(page != null? page: INITIAL_PAGE,PAGE_SIZE,filters,search);


        super.paginate(mav,games);

        mav.addObject("gamesData", games.getList());
        mav.addObject("currentPage", page);
        mav.addObject("orderCriteria", GameOrderCriteria.values());
        mav.addObject("orderDirections", OrderDirection.values());
        mav.addObject("searchField",search);
        mav.addObject("filters", filters);

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

}
