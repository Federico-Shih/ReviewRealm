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

import java.util.List;
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

        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        mav.addObject("games",gameService.getFavoriteGamesFromUser(userId));
        mav.addObject("profile",user.get());
        mav.addObject("reviews",reviewService.getUserReviews(userId));
        if (loggedUser != null) {
            mav.addObject("isProfileSelf", loggedUser.equals(user.get()));
            mav.addObject("following", userService.userFollowsId(loggedUser.getId(), userId));
        }

        return mav;
    }

    @RequestMapping(value = "/profile/following", method = RequestMethod.GET)
    public ModelAndView followingList() {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        ModelAndView mav = new ModelAndView("profile/friends-list");
        List<User> users = userService.getFollowing(loggedUser.getId());
        mav.addObject("users", users);
        mav.addObject("pageName", "profile.following.pagename");
        mav.addObject("usersLength", users.size());

        return mav;
    }

    @RequestMapping(value = "/profile/followers", method = RequestMethod.GET)
    public ModelAndView followersList() {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        ModelAndView mav = new ModelAndView("profile/friends-list");
        List<User> users = userService.getFollowers(loggedUser.getId());
        mav.addObject("users", users);
        mav.addObject("pageName", "profile.followers.pagename");
        mav.addObject("usersLength", users.size());

        return mav;
    }

    @RequestMapping(value = "/profile/follow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView followUser(@PathVariable(value = "id") long userId)
    {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        // todo - que hacer cuando falla
        try {
            userService.followUserById(loggedUser.getId(), userId);
        } catch (UserNotFoundException err) {
            return profile(userId);
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }

    @RequestMapping(value = "/profile/unfollow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView unfollowUser(@PathVariable(value = "id") long userId)
    {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        // todo - que hacer cuando falla
        try {
            userService.unfollowUserById(loggedUser.getId(), userId);
        } catch (UserNotFoundException err) {
            return profile(userId);
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }
}
