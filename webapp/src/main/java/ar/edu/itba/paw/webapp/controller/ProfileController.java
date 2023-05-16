package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.forms.FavoriteGamesForm;
import ar.edu.itba.paw.webapp.forms.NotificationsForm;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.forms.EditPreferencesForm;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProfileController extends PaginatedController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final UserService userService;
    private final ReviewService reviewService;
    private final GameService gameService;
    private final GenreService genreService;


    private static final String MODERATOR = "MODERATOR";

    private final static int RECOMMENDED_GAMES_COUNT = 3;
    @Autowired
    public ProfileController(UserService userService, ReviewService reviewService,
                             GameService gameService, GenreService genreService){
        super();
        this.userService = userService;
        this.reviewService = reviewService;
        this.gameService = gameService;
        this.genreService = genreService;
    }

    @RequestMapping(value = "/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable(value="id") long userId,
                                @RequestParam(value = "preferences-changed", required = false) Boolean preferencesChanged,
                                @RequestParam(value = "avatar-changed", required = false) Boolean avatarChanged,
                                @RequestParam(value = "notifications-changed", required = false) Boolean notificationsChanged
    )
    {
        final ModelAndView mav = new ModelAndView("profile/profile");
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent())
        {
            return new ModelAndView("static-components/not-found");
        }

        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        mav.addObject("preferencesChanged", preferencesChanged != null && preferencesChanged);
        mav.addObject("avatarChanged", avatarChanged != null && avatarChanged);
        mav.addObject("notificationsChanged", notificationsChanged != null && notificationsChanged);
        mav.addObject("games",gameService.getFavoriteGamesFromUser(userId));
        mav.addObject("profile",user.get());
        mav.addObject("userModerator", user.get().getRoles().contains(new Role(MODERATOR)));
        mav.addObject("reviews",reviewService.getUserReviews(user.get().getId(),loggedUser));
        FollowerFollowingCount ffc = userService.getFollowerFollowingCount(userId);
        mav.addObject("followerCount", ffc.getFollowerCount());
        mav.addObject("followingCount", ffc.getFollowingCount());
        if (loggedUser != null) {
            mav.addObject("isProfileSelf", loggedUser.equals(user.get()));
            mav.addObject("following", userService.userFollowsId(loggedUser.getId(), userId));
            mav.addObject("userHasNotSetPreferences", loggedUser.equals(user.get()) && !loggedUser.hasPreferencesSet());
        }
        return mav;
    }

    private ModelAndView friendsList(long userId, Boolean isFollowersPage, List<User> users) {
        ModelAndView mav = new ModelAndView("profile/friends-list");
        mav.addObject("users", users);
        mav.addObject("usersLength", users.size());
        mav.addObject("page", isFollowersPage ? "profile.followers.page" : "profile.following.page");

        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent())
        {
            return new ModelAndView("static-components/not-found");
        }
        mav.addObject("username",user.get().getUsername());

        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        if (loggedUser != null && loggedUser.equals(user.get())) {
            mav.addObject("isProfileSelf", true);
            mav.addObject("pageName", isFollowersPage ? "profile.ownfollowers.pagename" : "profile.ownfollowing.pagename");
        }
        else {
            mav.addObject("pageName", isFollowersPage ? "profile.followers.pagename" : "profile.following.pagename");
        }

        return mav;
    }

    @RequestMapping(value = "/profile/{id:\\d+}/following", method = RequestMethod.GET)
    public ModelAndView followingList(@PathVariable(value="id") long userId) {
        ModelAndView mav = new ModelAndView("profile/friends-list");
        List<User> users = userService.getFollowing(userId);
        return friendsList(userId, false, users);
    }

    @RequestMapping(value = "/profile/{id:\\d+}/followers", method = RequestMethod.GET)
    public ModelAndView followersList(@PathVariable(value="id") long userId) {
        ModelAndView mav = new ModelAndView("profile/friends-list");
        List<User> users = userService.getFollowers(userId);
        return friendsList(userId, true, users);
    }

    @RequestMapping(value = "/profile/follow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView followUser(@PathVariable(value = "id") long userId)
    {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        try {
            userService.followUserById(loggedUser.getId(), userId);
        } catch (UserNotFoundException err) {
            LOGGER.error("Following unexistant user: {}", userId);
            return profile(userId, false, false, false);
        } catch (RuntimeException err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
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
            LOGGER.error("Unfollowing unexistant user: {}", userId);
            return profile(userId, false, false, false);
        } catch (RuntimeException err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }

    @RequestMapping(value = "/profile/settings/", method= RequestMethod.GET)
    public ModelAndView accountSettings() {
        return new ModelAndView("profile/account-settings");
    }

    @RequestMapping(value = "/profile/settings/preferences", method= RequestMethod.GET)
    public ModelAndView editPreferences(@Valid @ModelAttribute("editPreferencesForm") final EditPreferencesForm form, @RequestParam(value = "nothingForDiscovery", defaultValue = "false") Boolean nothingForDiscovery){
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        final ModelAndView mav = new ModelAndView("profile/edit-preferences");
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent())
        {
            return new ModelAndView("static-components/not-found");
        }
        mav.addObject("profile",user.get());
        mav.addObject("availableGenres", genreService.getAllGenres().stream()
                .filter(genre -> !user.get().getPreferences().contains(genre)).collect(Collectors.toList()));
        mav.addObject("nothingForDiscovery", nothingForDiscovery);
        return mav;
    }

    @RequestMapping(value="/profile/settings/preferences", method = RequestMethod.POST)
    public ModelAndView submitEditPreferences(@Valid @ModelAttribute("editPreferencesForm") final EditPreferencesForm form,
                                              final BindingResult errors)
    {
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        if(errors.hasErrors()) {
            return editPreferences(form, false);
        }
        try {
            userService.setPreferences(new HashSet<>(form.getGenres()), userId);
        } catch (RuntimeException err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
            return editPreferences(form, false);
        }
        return new ModelAndView(String.format("redirect:/profile/%d?preferences-changed=true", userId));
    }

    @RequestMapping(value= "/profile/settings/avatar", method=RequestMethod.GET)
    public ModelAndView avatarSetting() {
        return new ModelAndView("profile/choose-avatar");
    }

    @RequestMapping(value="/profile/settings/avatar/{id:\\d+}", method=RequestMethod.POST)
    public ModelAndView changeAvatar(@PathVariable(value = "id") long imageId) {
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        try {
            userService.changeUserAvatar(userId, imageId);
        } catch (Exception err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
            return new ModelAndView("redirect:/profile/settings/avatar");
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }


    @RequestMapping(value="/profile/settings/favgames", method = RequestMethod.GET)
    public ModelAndView favgamesSetting(@Valid @ModelAttribute("favgamesForm") final FavoriteGamesForm Form) {
        ModelAndView mav = new ModelAndView("profile/choose-favgames");
        long id = AuthenticationHelper.getLoggedUser(userService).getId();
        List<Game> favgames = gameService.getFavoriteGamesFromUser(id);
        List<Game> favgamesCandidate = gameService.getPossibleFavGamesFromUser(id).stream()
                .filter(game -> !favgames.contains(game)).collect(Collectors.toList());
        mav.addObject("favgamescandidates", favgamesCandidate);
        mav.addObject("favgames", favgames);
        mav.addObject("nothingToShow", favgames.isEmpty() && favgamesCandidate.isEmpty());
        return mav;
    }

    @RequestMapping(value="/profile/settings/favgames", method = RequestMethod.POST)
    public ModelAndView changeFavgames(@Valid @ModelAttribute("favgamesForm") final FavoriteGamesForm form, final BindingResult errors) {
        if(errors.hasErrors()) {
            return favgamesSetting(form);
        }
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        try {
            gameService.setFavoriteGames(userId, form.getGameIds());
        } catch (Exception err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
            return favgamesSetting(form);
        }
        return new ModelAndView(String.format("redirect:/profile/%d?avatar-changed=true", userId));
    }

    @RequestMapping(value = "/profile/settings/notifications", method= RequestMethod.GET)
    public ModelAndView notificationSettings(@Valid @ModelAttribute("notificationsForm") final NotificationsForm form){
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        final ModelAndView mav = new ModelAndView("profile/notification-settings");
        mav.addObject("currentNotificationSettings",userService.getUserNotificationSettings(userId).entrySet());
        return mav;
    }

    @RequestMapping(value="/profile/settings/notifications", method = RequestMethod.POST)
    public ModelAndView submitNotificationSettings(@Valid @ModelAttribute("notificationsForm") final NotificationsForm form,
                                              final BindingResult errors)
    {
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();
        if(errors.hasErrors()) {
            return notificationSettings(form);
        }
        try {
            userService.setUserNotificationSettings(userId, form.getConvertedNotificationSettings());
        } catch (RuntimeException err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
            return notificationSettings(form);
        }
        return new ModelAndView(String.format("redirect:/profile/%d?notifications-changed=true", userId));
    }

    @RequestMapping(value="/for-you", method = RequestMethod.GET)
    public ModelAndView forYouPage(@RequestParam(value = "size", defaultValue = "6") Integer size,
                                   @RequestParam(value= "search", defaultValue = "") String search,
                                   @RequestParam(value= "endofqueue", defaultValue = "false") Boolean endOfQueue){
        final ModelAndView mav = new ModelAndView("profile/for-you");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        if(!search.isEmpty()) {
            Paginated<User> searchedUsers = userService.getSearchedUsers(1, 6, search);
            super.paginate(mav, searchedUsers);
            mav.addObject("users", searchedUsers.getList());
        }
        mav.addObject("search", search);
        mav.addObject("reviewsFollowing", reviewService.getReviewsFromFollowingByUser(loggedUser.getId(), size));
        mav.addObject("user", loggedUser);
        mav.addObject("userSetPreferences", loggedUser.hasPreferencesSet());
        mav.addObject("size", size);
        mav.addObject("endOfQueue", endOfQueue);

        return mav;
    }

    @RequestMapping(value="/for-you/discovery", method = RequestMethod.GET)
    public ModelAndView discoveryQueue(@RequestParam(value="position", defaultValue = "0") Integer position){
        final ModelAndView mav = new ModelAndView("profile/discovery");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        List<Game> recommendedGames = gameService.getRecommendationsOfGamesForUser(loggedUser);

        if(position<0){
            return new ModelAndView("static-components/not-found");
        }
        if(recommendedGames.isEmpty()){
            return new ModelAndView("redirect:/profile/settings/preferences?nothingForDiscovery=true");
        }
        if( position>=recommendedGames.size() ){
            return new ModelAndView("redirect:/for-you/?endofqueue=true");
        }

        Game game = recommendedGames.get(position);
        GameReviewData reviewData = gameService.getReviewsByGameId(game.getId(),loggedUser);

        //Si esta el juego entonces si o si estan las reviews aunque sean vacias, no hay que chequear
        mav.addObject("game",game);
        mav.addObject("gameReviewData", reviewData);
        mav.addObject("positionInQueue",position);

        return mav;
    }
}
