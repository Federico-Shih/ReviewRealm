package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ReviewController {
    private UserService userService;
    private GameService gameService;
    private ReviewService reviewService;

    @Autowired
    public ReviewController(UserService userService, GameService gameService, ReviewService reviewService) {
        this.userService = userService;
        this.gameService = gameService;
        this.reviewService = reviewService;
    }

    @RequestMapping(value="/review/submit", method = RequestMethod.GET)
    public ModelAndView createReviewForm(@RequestParam(value = "gameId", required = false) Integer gameId) {
        Game reviewedGame = gameService.getGameById(gameId);
        if (reviewedGame == null) {
            return new ModelAndView("not-found");
        }
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", reviewedGame);
        return mav;
    }

    @RequestMapping(value="/review/submit/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView submitReview(
            @PathVariable(value = "id") Integer gameId,
            @RequestParam(value = "review-title") String title,
            @RequestParam(value = "review-content") String content,
            @RequestParam(value = "review-author") String email,
            @RequestParam(value = "review-rating") Integer rating
            ) {
        reviewService.createReview(title, content, rating, email, gameId);
        ModelAndView mav = new ModelAndView("/review/submit-review");
        mav.addObject("game", gameService.getGameById(gameId));
        return mav;
    }
}
