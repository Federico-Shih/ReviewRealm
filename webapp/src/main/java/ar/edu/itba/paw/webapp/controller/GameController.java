package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class GameController {

    private ReviewService rs;
    private GameService gs;

    private static final int MAX_PAGES_PAGINATION = 6;

    private static final int PAGE_SIZE = 10;

    private static final int INITIAL_PAGE = 1;

    @Autowired
    public GameController(ReviewService rs, GameService gs) {
        this.rs = rs;
        this.gs = gs;
    }

    @RequestMapping("/game/{id:\\d+}")
    public ModelAndView game_details(@PathVariable("id") final Long gameId){
        final ModelAndView mav =  new ModelAndView("games/game-details");
        Optional<Game> game = gs.getGameById(gameId);
        if(game.isPresent()){
            mav.addObject("game",game.get());
            List<Review> reviewList = gs.getReviewsByGameId(gameId);
            //Si esta el juego entonces si o si estan las reviews aunque sean vacias, no hay que chequear
            mav.addObject("reviews", reviewList);

        }else{
            return new ModelAndView("static-components/not-found");
        }
        return mav;
    }

    @RequestMapping("/game/list")
    public ModelAndView gameList(@RequestParam(value = "page", defaultValue = "1") Integer page){
        final ModelAndView mav = new ModelAndView("games/game-list");
        Paginated<Game> games = gs.getAllGames(page,PAGE_SIZE);
        if(games.getTotalPages() > MAX_PAGES_PAGINATION){
            int offset = (page <= (MAX_PAGES_PAGINATION/2))? 0 : page - (MAX_PAGES_PAGINATION/2);
            mav.addObject("maxPages", Math.min(MAX_PAGES_PAGINATION + offset, games.getTotalPages()));
            mav.addObject("initialPage", Math.min(INITIAL_PAGE + offset, (games.getTotalPages() - MAX_PAGES_PAGINATION)));
        }else{
            mav.addObject("maxPages", games.getTotalPages());
            mav.addObject("initialPage", INITIAL_PAGE);
        }
        mav.addObject("games", games.getList());
        mav.addObject("currentPage", page);
        return mav;
    }
}
