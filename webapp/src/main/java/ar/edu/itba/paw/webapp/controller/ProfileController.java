package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.dtos.Page;
import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.enums.Mission;
import ar.edu.itba.paw.enums.RoleType;
import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.datacontainers.ContentTab;
import ar.edu.itba.paw.webapp.controller.helpers.PaginationHelper;
import ar.edu.itba.paw.webapp.controller.helpers.QueryHelper;
import ar.edu.itba.paw.webapp.exceptions.ObjectNotFoundException;
import ar.edu.itba.paw.webapp.forms.EditPreferencesForm;
import ar.edu.itba.paw.webapp.forms.FavoriteGamesForm;
import ar.edu.itba.paw.webapp.forms.NotificationsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
public class ProfileController{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileController.class);
    private final UserService userService;
    private final ReviewService reviewService;
    private final GameService gameService;
    private final static int DEFAULT_PAGE_SIZE = 8;

    @Autowired
    public ProfileController(UserService userService, ReviewService reviewService,
                             GameService gameService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.gameService = gameService;
    }

    @RequestMapping(value = "/profile/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView profile(@PathVariable(value="id") long userId,
                                @RequestParam(value = "preferences-changed", required = false) Boolean preferencesChanged,
                                @RequestParam(value = "avatar-changed", required = false) Boolean avatarChanged,
                                @RequestParam(value = "notifications-changed", required = false) Boolean notificationsChanged,
                                @RequestParam(value = "favorite_games_changed", required = false) Boolean favoriteGamesChanged,
                                @RequestParam(value = "page", defaultValue = "1") Integer page,
                                @RequestParam(value = "pagesize", defaultValue ="8" ) Integer pageSize
    )
    {
        final ModelAndView mav = new ModelAndView("profile/profile");
        Optional<User> user = userService.getUserById(userId);
        if(!user.isPresent()) {
            throw new ObjectNotFoundException();
        }

        if(pageSize==null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        mav.addObject("preferencesChanged", preferencesChanged != null && preferencesChanged);
        mav.addObject("avatarChanged", avatarChanged != null && avatarChanged);
        mav.addObject("notificationsChanged", notificationsChanged != null && notificationsChanged);
        mav.addObject("favoriteGamesChanged", favoriteGamesChanged != null && favoriteGamesChanged);

        mav.addObject("games",gameService.getFavoriteGamesFromUser(userId));
        mav.addObject("profile",user.get());
        mav.addObject("userModerator", user.get().getRoles().contains(RoleType.MODERATOR));

        Paginated<Review> userReviews = reviewService.getUserReviews(Page.with(page, pageSize),user.get().getId(), loggedUser != null ? loggedUser.getId() : null);

        PaginationHelper.paginate(mav,userReviews);

        mav.addObject("reviews",userReviews.getList());
        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
        queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));

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
        if(!user.isPresent()) {
            throw new ObjectNotFoundException();
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
        List<User> users = userService.getFollowing(userId);
        return friendsList(userId, false, users);
    }

    @RequestMapping(value = "/profile/{id:\\d+}/followers", method = RequestMethod.GET)
    public ModelAndView followersList(@PathVariable(value="id") long userId) {
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
        } catch (RuntimeException err) {
            LOGGER.error("Unexpected error: {}", err.getMessage());
        }
        return new ModelAndView(String.format("redirect:/profile/%d", userId));
    }

    @RequestMapping(value = "/profile/unfollow/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView unfollowUser(@PathVariable(value = "id") long userId)
    {
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        try {
            userService.unfollowUserById(loggedUser.getId(), userId);
        } catch (UserNotFoundException err) {
            LOGGER.error("Unfollowing unexistant user: {}", userId);
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
        if(!user.isPresent()) {
            throw new ObjectNotFoundException();
        }
        mav.addObject("profile",user.get());
        mav.addObject("availableGenres", Arrays.stream(Genre.values())
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
        return new ModelAndView(String.format("redirect:/profile/%d?preferences-changed=true", userId));
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
    public ModelAndView forYouPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize,
                                   @RequestParam(value= "content", defaultValue = "FOLLOWING") String content,
                                   @RequestParam(value= "endofqueue", defaultValue = "false") Boolean endOfQueue){
        final ModelAndView mav = new ModelAndView("profile/for-you");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        if(pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        ContentTab contentTab;
        try {
            contentTab = ContentTab.valueOf(content);
        } catch (IllegalArgumentException e){
            contentTab = ContentTab.FOLLOWING;
        }
        Page pageToPaginate = Page.with(page, pageSize);
        Paginated<Review> reviews = new Paginated<>(pageToPaginate.getPageNumber(), pageToPaginate.getPageSize(), 0, new ArrayList<>());
        boolean noFollowingAndFollowTab = false;
        switch (contentTab){
            case FOLLOWING:
                reviews = reviewService.getReviewsFromFollowingByUser(loggedUser.getId(), pageToPaginate);
                noFollowingAndFollowTab = loggedUser.getFollowing().isEmpty();
                break;
            case NEW:
                reviews = reviewService.getNewReviewsExcludingActiveUser(pageToPaginate,loggedUser.getId());
                break;
            case RECOMMENDED:
                reviews = reviewService.getRecommendedReviewsByUser(loggedUser.getId(), pageToPaginate);
                break;
            default:
                break;
        }

        PaginationHelper.paginate(mav,reviews);

        mav.addObject("reviews",reviews.getList());

        mav.addObject("user", loggedUser);
        mav.addObject("userSetPreferences", loggedUser.hasPreferencesSet());
        mav.addObject("pagesize", pageSize);
        mav.addObject("contentTab", contentTab);
        mav.addObject("contentTabHeaderCode", contentTab.getHeaderCode());
        if(noFollowingAndFollowTab){
            mav.addObject("contentNotFoundCode","for-you.reviews.following.nofollowing");
        }else{
            mav.addObject("contentNotFoundCode",contentTab.getNotFoundCode());
        }
        mav.addObject("endOfQueue", endOfQueue);
        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();

        queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
        queriesToKeepAtPageChange.add(new Pair<>("content", contentTab.toString()));

        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));
        return mav;
    }

    @RequestMapping(value="/for-you/discovery", method = RequestMethod.GET)
    public ModelAndView discoveryQueue(@RequestParam(value="position", defaultValue = "0") Integer position,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize){
        final ModelAndView mav = new ModelAndView("games/game-details");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        List<Game> recommendedGames = gameService.getRecommendationsOfGamesForUser(loggedUser.getId());

        if(position == null || position < 0){
            position = 0;
        }

        if(pageSize == null || pageSize<1){
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if(recommendedGames.isEmpty()){
            return new ModelAndView("redirect:/profile/settings/preferences?nothingForDiscovery=true");
        }
        if( position>=recommendedGames.size() ){
            return new ModelAndView("redirect:/for-you/?endofqueue=true");
        }

        Game game = recommendedGames.get(position);
        mav.addObject("game",game);
        GameReviewData reviewData = gameService.getGameReviewDataByGameId(game.getId());
        Paginated<Review> reviews = reviewService.getReviewsFromGame(Page.with(page, pageSize), game.getId(), loggedUser.getId(), true);
        PaginationHelper.paginate(mav, reviews);
        mav.addObject("gameReviewData", reviewData);
        mav.addObject("reviews", reviews.getList());
        mav.addObject("discoveryQueue",true);
        mav.addObject("positionInQueue", position);
        mav.addObject("queryString", "?position=" + position+"&");
        return mav;
    }

    @RequestMapping(value = "/profile/missions", method = RequestMethod.GET)
    public ModelAndView missionsPage() {
        ModelAndView mav = new ModelAndView("profile/missions");
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        Map<Mission, MissionProgress> userMissions = loggedUser.getMissions().stream().collect(Collectors.toMap(MissionProgress::getMission, Function.identity()));
        List<MissionProgress> currentProgresses = Arrays.stream(Mission.values())
            .filter(
                mission -> mission.getRoleType() == null ||
                        loggedUser.getRoles().stream().anyMatch(
                            (role) -> role.equals(mission.getRoleType())
                        )
            ).map(mission -> userMissions.getOrDefault(mission, new MissionProgress(loggedUser, mission, 0f, null, 0))).collect(Collectors.toList());
        mav.addObject("missions", currentProgresses);
        mav.addObject("level", loggedUser.getLevel());
        mav.addObject("xp", loggedUser.getXp());
        mav.addObject("currentLevelXp", loggedUser.getXp() % 100);
        return mav;
    }
}
