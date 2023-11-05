package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.FeedbackType;
import ar.edu.itba.paw.exceptions.ReviewAlreadyExistsException;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.Review;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReportService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AccessControl;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentReviewId;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentUserId;
import ar.edu.itba.paw.webapp.controller.forms.EditReviewForm;
import ar.edu.itba.paw.webapp.controller.forms.SubmitReviewForm;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.ReviewSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.FeedbackResponse;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponseHelper;
import ar.edu.itba.paw.webapp.controller.responses.ReviewResponse;
import ar.edu.itba.paw.webapp.validators.FeedbackTypeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/api/reviews")
@Component
public class ReviewController{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);
    @Context
    private UriInfo uriInfo;
    private final GameService gameService;
    @Autowired
    private AccessControl accessControl;
    private final ReviewService reviewService;
    private final UserService userService;
    private final ReportService reportService;
    private static final int PAGE_SIZE = 8;
    private static final int INITIAL_PAGE = 1;
    private static final int MAX_SEARCH_RESULTS = 6;

    @Autowired
    public ReviewController(GameService gameService, ReviewService reviewService, UserService userService, ReportService reportService) {
        this.gameService = gameService;
        this.reviewService = reviewService;
        this.userService = userService;
        this.reportService = reportService;

    }

    /*
      TODO:
       GET /reviews
       POST /reviews
       DELETE /reviews/{reviewid}
       GET /reviews/{reviewid}
       PATCH /reviews/{reviewid}
       GET /reviews/{id}/feedback/{userId}
       POST /reviews/{id}/feedback{?feedbackType}
       DELETE /reviews/{id}/feedback/{userId}
    */


    @GET
    @Produces(VndType.APPLICATION_REVIEW_LIST)
    public Response getReviews(@Valid @BeanParam ReviewSearchQuery reviewSearchQuery) {
        User user = AuthenticationHelper.getLoggedUser(userService);
        final Paginated<Review> reviews = reviewService.searchReviews(reviewSearchQuery.getPage(), reviewSearchQuery.getFilter(), reviewSearchQuery.getOrdering(), user!=null? user.getId() : null);
        if (reviews.getTotalPages() == 0 || reviews.getList().isEmpty()) {
            return Response.noContent().build();
        }
        List<ReviewResponse> reviewResponseList = reviews.getList().stream().map((review) -> ReviewResponse.fromEntity(uriInfo, review)).collect(Collectors.toList());
        return PaginatedResponseHelper.fromPaginated(uriInfo, reviewResponseList, reviews).build();
    }

    @POST
    @Produces(VndType.APPLICATION_REVIEW)
    public Response createReview(@Valid final SubmitReviewForm form) throws ReviewAlreadyExistsException {
        User author = AuthenticationHelper.getLoggedUser(userService);
        Review createdReview = reviewService.createReview(
                form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                author.getId(),
                form.getGameId(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/reviews").path(createdReview.getId().toString()).build())
                .entity(ReviewResponse.fromEntity(uriInfo, createdReview))
                .build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{id}")
    @PreAuthorize("@accessControl.checkReviewAuthorOwnerOrMod(#id)")
    public Response deleteReview(@Valid @ExistentReviewId @PathParam("id") long id) { // Todo: Hace falta el valid?
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);

        if (!reviewService.deleteReviewById(id, loggedUser.getId())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("{id}")
    @Produces(VndType.APPLICATION_REVIEW)
    public Response getReviewById(@Valid @ExistentReviewId @PathParam("id") final long id) {
        Optional<Review> possibleReview = reviewService.getReviewById(id, AuthenticationHelper.getLoggedUser(userService).getId());
        if (!possibleReview.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(ReviewResponse.fromEntity(uriInfo, possibleReview.get())).build();
    }



    @PATCH
    @Path("{id}")
    @Consumes(VndType.APPLICATION_REVIEW_PATCH)
    @PreAuthorize("@accessControl.checkReviewAuthorOwnerOrMod(#id)")
    public Response patchReview(@Valid @ExistentReviewId @PathParam("id") final long id, @Valid final EditReviewForm form) {
        reviewService.updateReview(id, form.getReviewTitle(),
                form.getReviewContent(),
                form.getReviewRating(),
                form.getDifficultyEnum(),
                form.getGameLengthSeconds(),
                form.getPlatformEnum(),
                form.getCompleted(),
                form.getReplayability()
        );
        return Response.noContent().build();
    }

    // Si no existe, devuelve null, no me parece mal. TODO: quiza considerar 404?
    @GET
    @Path("{reviewId}/feedback/{userId}")
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    public Response getReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @ExistentUserId @PathParam("userId") long userId) {
        Optional<Review> perhapsReview = reviewService.getReviewById(reviewId, userId);
        return Response.ok(FeedbackResponse.fromEntity(uriInfo, userId, perhapsReview.get().getId(), perhapsReview.get().getFeedback())).build();
    }

    @POST
    @Path("{reviewId}/feedback")
    @Produces(VndType.APPLICATION_REVIEW_FEEDBACK)
    @PreAuthorize("@accessControl.checkUserIsNotAuthor(#reviewId)")
    public Response createReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @BeanParam FeedbackTypeBean feedbackType) {
        FeedbackType fb = feedbackType.transformToEnum();
        long userId = AuthenticationHelper.getLoggedUser(userService).getId();

        reviewService.updateOrCreateReviewFeedback(reviewId,userId,fb);
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("reviews").path(String.valueOf(reviewId)).path("feedback").path(String.valueOf(userId)).build())
                .entity(FeedbackResponse.fromEntity(uriInfo, userId, reviewId, fb))
                .build();
    }


    @DELETE
    @Path("{reviewId}/feedback/{userId}")
    @PreAuthorize("@accessControl.checkLoggedIsCreatorOfFeedback(#reviewId, #userId)")
    public Response deleteReviewFeedback(@Valid @ExistentReviewId @PathParam("reviewId") long reviewId, @Valid @ExistentUserId @PathParam("userId") long userId) {
        return reviewService.deleteReviewFeedback(reviewId,userId) ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

//    @RequestMapping(value = "/review/submit/search")
//    public ModelAndView searchGames(@RequestParam(value = "searchquery", defaultValue = "") String searchquery,
//                                    @RequestParam(value = "page", defaultValue = "1") Integer page,
//                                    @RequestParam(value = "pagesize", defaultValue = "6") Integer pageSize) {
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        ModelAndView mav = new ModelAndView("/review/submit-review-search");
//
//        if(pageSize == null || pageSize < 1) {
//            pageSize = MAX_SEARCH_RESULTS;
//        }
//
//        if(searchquery.isEmpty()){
//            List<Game> list = gameService.getRecommendationsOfGamesForUser(loggedUser.getId());
//            mav.addObject("games", list.subList(0, Math.min(list.size(), MAX_SEARCH_RESULTS)));
//        } else {
//            Paginated<Game> games = gameService.searchGamesNotReviewedByUser(
//                    Page.with(page, pageSize),
//                    searchquery,
//                    new Ordering<>(OrderDirection.ASCENDING, GameOrderCriteria.NAME),
//                    loggedUser.getId()
//            );
//            PaginationHelper.paginate(mav,games);
//            mav.addObject("games", games.getList());
//        }
//        mav.addObject("searchField", searchquery);
//
//        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
//        queriesToKeepAtPageChange.add(new Pair<>("searchquery", searchquery));
//        queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
//        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));
//
//        return mav;
//    }
//
//    @RequestMapping(value = "/review/submit/{gameId:\\d+}", method = RequestMethod.GET)
//    public ModelAndView createReviewForm(@PathVariable(value = "gameId") Long gameId,
//                                         @ModelAttribute("reviewForm") final SubmitReviewForm form) {
//        ModelAndView mav = new ModelAndView("/review/submit-review");
//        if(gameId == 0) { //If gameId not specified
//            return new ModelAndView("redirect:/review/submit/search");
//        }
//        Game reviewedGame = gameService.getGameById(gameId).orElseThrow(ObjectNotFoundException::new);
//
//        mav.addObject("edit", false);
//        mav.addObject("game", reviewedGame);
//        mav.addObject("selectedGameId", gameId);
//        mav.addObject("platforms", Platform.values());
//        mav.addObject("difficulties", Difficulty.values());
//        mav.addObject("units", GamelengthUnit.values());
//        return mav;
//    }
//    @RequestMapping(value = "/review/submit", method = RequestMethod.GET)
//    public ModelAndView createReviewForm() {
//        return new ModelAndView("redirect:/review/submit/search");
//    }
//
//    @RequestMapping(value = "/review/{id:\\d+}", method = RequestMethod.GET)
//    public ModelAndView reviewDetails(
//            @PathVariable(value = "id") Long reviewId,
//            @RequestParam(value = "created", required = false) Boolean created,
//            @RequestParam(value = "reported", required = false) Boolean reported
//    ) {
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        Review review = reviewService.getReviewById(reviewId, loggedUser != null ? loggedUser.getId() : null).orElseThrow(ObjectNotFoundException::new);
//
//        Collection<? extends GrantedAuthority> roles = AuthenticationHelper.getAuthorities();
//        ModelAndView mav = new ModelAndView("/review/review-details");
//        mav.addObject("review", review);
//        mav.addObject("created", created != null && created);
//        mav.addObject("reported", reported != null && reported);
//        mav.addObject("isReported", (loggedUser != null)? reportService.isReported(reviewId, loggedUser.getId()): true);
//        mav.addObject("reportValues",ReportReason.values());
//        mav.addObject("game", review.getReviewedGame());
//        mav.addObject("isModerated", roles.contains(new SimpleGrantedAuthority(String.format("ROLE_%s", RoleType.MODERATOR.getRole()))));
//        mav.addObject("isOwner", loggedUser != null && loggedUser.getId().equals(review.getAuthor().getId()));
//        return mav;
//    }
//
//    @RequestMapping(value = "/review/submit/{id:\\d+}", method = RequestMethod.POST)
//    public ModelAndView submitReview(
//            @PathVariable(value = "id") Long gameId,
//            @Valid @ModelAttribute("reviewForm") final SubmitReviewForm form,
//            final BindingResult errors
//    ) {
//        if (errors.hasErrors()) {
//            return createReviewForm(gameId, form);
//        }
//        User author = AuthenticationHelper.getLoggedUser(userService);
//        Review createdReview = reviewService.createReview(
//                form.getReviewTitle(),
//                form.getReviewContent(),
//                form.getReviewRating(),
//                author.getId(),
//                gameId,
//                form.getDifficultyEnum(),
//                form.getGameLengthSeconds(),
//                form.getPlatformEnum(),
//                form.getCompleted(),
//                form.getReplayability()
//        );
//        return new ModelAndView("redirect:/review/" + createdReview.getId() + "?created=true");
//    }
//
//
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public ModelAndView reviewList(
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
//            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
//            @RequestParam(value = "f-tpl", defaultValue = "") String timePlayedFilter,
//            @RequestParam(value = "f-prf", defaultValue = "") List<Integer> preferencesFilter,
//            @RequestParam(value = "f-plt", defaultValue = "") List<Integer> platformsFilter,
//            @RequestParam(value = "f-dif", defaultValue = "") List<Integer> difficultyFilter,
//            @RequestParam(value = "f-cpt", defaultValue = "") Boolean completedFilter,
//            @RequestParam(value = "f-rpl", defaultValue = "") Boolean replayableFilter,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "search", defaultValue = "") String search
//    ) {
//        final ModelAndView mav = new ModelAndView("review/review-list");
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        List<Genre> allGenres = Arrays.asList(Genre.values());
//
//        if(pageSize == null || pageSize < 1) {
//            pageSize = PAGE_SIZE;
//        }
//
//        double minTimePlayed = 0;
//        try {
//            minTimePlayed = Double.parseDouble(timePlayedFilter);
//        } catch (Exception ignored) {}
//
//        ReviewFilter searchFilter = new ReviewFilterBuilder()
//                .withGameGenres(genresFilter)
//                .withPlatforms(
//                        platformsFilter.stream()
//                        .map(Platform::getById)
//                        .filter(Optional::isPresent)
//                        .map(Optional::get)
//                        .collect(Collectors.toList())
//                )
//                .withDifficulties(
//                        difficultyFilter.stream()
//                        .map(Difficulty::getById)
//                        .filter(Optional::isPresent)
//                        .map(Optional::get)
//                        .collect(Collectors.toList())
//                )
//                .withCompleted((completedFilter != null && completedFilter) ? true : null)
//                .withReplayable((replayableFilter!=null && replayableFilter) ? true : null)
//                .withAuthorGenres(preferencesFilter)
//                .withReviewContent(search)
//                .withMinTimePlayed(minTimePlayed)
//                .build();
//
//
//        Paginated<Review> reviewPaginated = reviewService.searchReviews(
//                Page.with(page != null ? page : INITIAL_PAGE, pageSize),
//                searchFilter,
//                new Ordering<>(OrderDirection.fromValue(orderDirection), ReviewOrderCriteria.fromValue(orderCriteria)),
//                loggedUser != null ? loggedUser.getId() : null
//        );
//
//        PaginationHelper.paginate(mav,reviewPaginated);
//
//        mav.addObject("reviews", reviewPaginated.getList());
//        mav.addObject("totalReviews", reviewPaginated.getTotalPages());
//        mav.addObject("orderCriteria", ReviewOrderCriteria.values());
//        mav.addObject("orderDirections", OrderDirection.values());
//        mav.addObject("searchField", search);
//        mav.addObject("genresFilter", new FilteredList<>(allGenres, (genre) -> genresFilter.contains(genre.getId())));
//        mav.addObject("preferencesFilter", new FilteredList<>(allGenres, (preference) -> preferencesFilter.contains(preference.getId())));
//        mav.addObject("platformsFilter", new FilteredList<>(Arrays.asList(Platform.values()), (platform) -> platformsFilter.contains(platform.getId())));
//        mav.addObject("difficultiesFilter", new FilteredList<>(Arrays.asList(Difficulty.values()), (difficulty) -> difficultyFilter.contains(difficulty.getId())));
//        mav.addObject("completedFilter", completedFilter);
//        mav.addObject("replayableFilter", replayableFilter);
//        mav.addObject("selectedOrderDirection", OrderDirection.fromValue(orderDirection));
//        mav.addObject("selectedOrderCriteria", ReviewOrderCriteria.fromValue(orderCriteria));
//        mav.addObject("minTimePlayed", minTimePlayed);
//        if(orderCriteria== 0 && orderDirection == 0 && genresFilter.isEmpty() && timePlayedFilter.isEmpty() && preferencesFilter.isEmpty() && platformsFilter.isEmpty() && difficultyFilter.isEmpty() && completedFilter == null && replayableFilter == null) {
//            mav.addObject("showResetFiltersButton", false);
//        } else {
//            mav.addObject("showResetFiltersButton", true);
//        }
//
//        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
//        queriesToKeepAtPageChange.add(Pair.of("o-crit", orderCriteria));
//        queriesToKeepAtPageChange.add(Pair.of("o-dir", orderDirection));
//        queriesToKeepAtPageChange.add(Pair.of("pageSize", pageSize));
//        queriesToKeepAtPageChange.add(Pair.of("search", search));
//        queriesToKeepAtPageChange.addAll(genresFilter.stream().map((value) -> Pair.of("f-gen", (Object)value)).collect(Collectors.toList()));
//        queriesToKeepAtPageChange.addAll(preferencesFilter.stream().map((value) -> Pair.of("f-prf", (Object)value)).collect(Collectors.toList()));
//        queriesToKeepAtPageChange.addAll(platformsFilter.stream().map((value) -> Pair.of("f-plt", (Object)value)).collect(Collectors.toList()));
//        queriesToKeepAtPageChange.addAll(difficultyFilter.stream().map((value) -> Pair.of("f-dif", (Object)value)).collect(Collectors.toList()));
//        if(completedFilter!=null)
//            queriesToKeepAtPageChange.add(Pair.of("f-cpt", completedFilter));
//        if(replayableFilter!=null)
//            queriesToKeepAtPageChange.add(Pair.of("f-rpl", replayableFilter));
//        queriesToKeepAtPageChange.add(Pair.of("f-tpl", timePlayedFilter));
//
//        mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));
//
//        List<Pair<String, Object>> queriesToKeepAtRemoveFilters = new ArrayList<>();
//
//        mav.addObject("queriesToKeepAtRemoveFilters", QueryHelper.toQueryString(queriesToKeepAtRemoveFilters));
//
//        if(loggedUser == null || !loggedUser.hasPreferencesSet()) {
//            mav.addObject("showFavoritesShortcut", false);
//        } else {
//            mav.addObject("showFavoritesShortcut", true);
//            mav.addObject("userPreferences", loggedUser.getPreferences().stream().map(Genre::getId).collect(Collectors.toList()));
//        }
//        return mav;
//    }
//
//    @RequestMapping(value = "/review/delete/{id:\\d+}", method = RequestMethod.POST)
//    public ModelAndView deleteReview(@PathVariable(value = "id") Long id) {
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        long gameId = reviewService.getReviewById(id, null)
//                .orElseThrow(ObjectNotFoundException::new)
//                .getReviewedGame().getId();
//        boolean deleted = reviewService.deleteReviewById(id, loggedUser.getId());
//        if (!deleted) {
//            return reviewDetails(id, false,false);
//        }
//        return new ModelAndView("redirect:/game/" + gameId);
//    }
//
//    @RequestMapping(value = "/review/feedback/{id:\\d+}", method = RequestMethod.POST)
//    public ModelAndView updateReviewFeedback(@PathVariable(value = "id") Long id,
//                                   @RequestParam(value = "feedback") String feedback,
//                                   @RequestParam(value = "url", defaultValue = "/") String url){
//        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
//        FeedbackType fb;
//        try {
//            fb = FeedbackType.valueOf(feedback);
//        } catch (IllegalArgumentException e){
//            throw new ObjectNotFoundException();
//        }
//        reviewService.updateOrCreateReviewFeedback(id, loggedUser.getId(), fb);
//        return new ModelAndView("redirect:" + url);
//    }
//
//    @RequestMapping(value = "/review/{id:\\d+}/edit", method = RequestMethod.GET)
//    public ModelAndView editReviewForm(@PathVariable("id") Long reviewId, @ModelAttribute("reviewForm") EditReviewForm form) {
//        User user = AuthenticationHelper.getLoggedUser(userService);
//        Review review = reviewService.getReviewById(reviewId, user.getId()).orElseThrow(ObjectNotFoundException::new);
//        form.fromReview(review);
//        ModelAndView mav = new ModelAndView("review/submit-review");
//        mav.addObject("edit", true);
//        mav.addObject("game", review.getReviewedGame());
//        mav.addObject("id", reviewId);
//        mav.addObject("platforms", Platform.values());
//        mav.addObject("difficulties", Difficulty.values());
//        mav.addObject("units", GamelengthUnit.values());
//        return mav;
//    }
//
//    @RequestMapping(value = "/review/{id:\\d+}/edit", method = RequestMethod.POST)
//    public ModelAndView editReview(@PathVariable("id") Long reviewId, @Valid @ModelAttribute("reviewForm") EditReviewForm form, final BindingResult errors) {
//        if (errors.hasErrors()) {
//            return editReviewForm(reviewId, form);
//        }
//        Review update = reviewService.updateReview(reviewId, form.getReviewTitle(), form.getReviewContent(), form.getReviewRating(), form.getDifficultyEnum(), form.getGameLengthSeconds(), form.getPlatformEnum(),  form.getReplayability(),  form.getReplayability());
//        return new ModelAndView("redirect:/review/" + update.getId());
//    }
//
//    @ExceptionHandler(ReviewAlreadyExistsException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ModelAndView reviewExists(ReviewAlreadyExistsException exception) {
//        User user = AuthenticationHelper.getLoggedUser(userService);
//        Review review = reviewService.getReviewOfUserForGame(user.getId(), exception.getReviewedGame().getId()).orElseThrow(ObjectNotFoundException::new);
//        return reviewDetails(review.getId(), false,false);
//    }
}
