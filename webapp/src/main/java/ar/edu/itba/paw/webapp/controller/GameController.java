package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class GameController {

    private ReviewService rs;
    private GameService gs;

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
            return new ModelAndView("not-found");
        }
        return mav;
    }

    @RequestMapping("/game/list")
    public ModelAndView gameList(){
        final ModelAndView mav = new ModelAndView("games/game-list");
        mav.addObject("games", gs.getAllGames());
        return mav;
    }
}
