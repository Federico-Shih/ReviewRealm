package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class ProfileController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final GameService gameService;

    @Autowired
    public ProfileController(UserService userService, ReviewService reviewService, GameService gameService){
        this.userService = userService;
        this.reviewService = reviewService;
        this.gameService = gameService;
    }

    @RequestMapping(value = "/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable(value="id") long userId)
    {
        final ModelAndView mav = new ModelAndView("profile/profile");
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent())
        {
            return new ModelAndView("static-components/not-found");
        }

        Optional<User> loggedUser = AuthenticationHelper.getLoggedUser(userService);

        mav.addObject("games",gameService.getFavoriteGamesFromUser(userId));
        mav.addObject("profile",user.get());
        mav.addObject("reviews",reviewService.getUserReviews(userId));
        mav.addObject("isProfileSelf", loggedUser.isPresent() && loggedUser.get().equals(user.get()));
        boolean follows = userService.userFollowsId(loggedUser.get().getId(), userId);
        System.out.println(follows);
        loggedUser.ifPresent(value -> mav.addObject("following", follows));

        return mav;
    }

    @RequestMapping(value = "/profile/follow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView followUser(@PathVariable(value = "id") long userId)
    {
        Optional<User> loggedUser = AuthenticationHelper.getLoggedUser(userService);
        if (!loggedUser.isPresent()) {
            return new ModelAndView("redirect:/login");
        }
        // todo - que hacer cuando falla
        try {
            userService.followUserById(loggedUser.get().getId(), userId);
        } catch (UserNotFoundException err) {
            return profile(userId);
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }

    @RequestMapping(value = "/profile/unfollow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView unfollowUser(@PathVariable(value = "id") long userId)
    {
        Optional<User> loggedUser = AuthenticationHelper.getLoggedUser(userService);
        if (!loggedUser.isPresent()) {
            return new ModelAndView("redirect:/login");
        }
        // todo - que hacer cuando falla
        try {
            userService.unfollowUserById(loggedUser.get().getId(), userId);
        } catch (UserNotFoundException err) {
            return profile(userId);
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }
}
