package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.servicesinterfaces.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GameListController {
    final private GameService gs;

    @Autowired
    public GameListController(GameService gs) {
        this.gs = gs;
    }

    @RequestMapping("/games-list")
    public ModelAndView gameList(){
        final ModelAndView mav = new ModelAndView("games/game-list");
        mav.addObject("games", gs.getAllGames());
        return mav;
    }
}
