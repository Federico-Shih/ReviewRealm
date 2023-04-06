package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GameController {

    private ReviewService rs;
    private GameService gs;

    @Autowired
    public GameController(ReviewService rs, GameService gs) {
        this.rs = rs;
        this.gs = gs;
    }

    @RequestMapping("/games")
    public ModelAndView game_details(){
        final ModelAndView mav =  new ModelAndView("games/game_details");
        mav.addObject("game",gs.getGameById(1));
        mav.addObject("reviews",gs.getReviewsByGameId(1));
        return mav;
    }
}
