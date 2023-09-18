package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.ReviewService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.controller.responses.GameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Optional;

@Path("/games")
@Component
public class GameController extends UriInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    private final GameService gs;
    private final UserService us;
    private final ReviewService rs;

    private static final int DEFAULT_REVIEW_PAGE_SIZE = 10;
    private static final int PAGE_SIZE = 10;
    private static final int INITIAL_PAGE = 1;
    private static final int SMALL_LIST_PAGE_SIZE = 2;

    @Context
    private UriInfo uriInfo;


    @Autowired
    public GameController(GameService gs, UserService us, ReviewService rs) {
        this.gs = gs;
        this.us = us;
        this.rs = rs;
    }

    @GET
    @Path("{id:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGameById(final Long gameId) {
        Optional<Game> game = gs.getGameById(gameId);
        if (game.isPresent()) {
            return Response.ok(GameResponse.fromEntity(uriInfo,game.get(),gs.getGameReviewDataByGameId(gameId))).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getGames(@Valid @BeanParam GameSearchQuery gameSearchQuery){
//        final Paginated<Game> games = gs.searchGames(gameSearchQuery.getPage(), gameSearchQuery.getFilter(),gameSearchQuery.getOrdering());
//        List<GameResponse> gameResponseList = games.getList().stream().map(this.currifyUriInfo(GameResponse::fromEntity)).collect(Collectors.toList();
//        Response.ResponseBuilder response = Response.ok(PaginatedResponse.fromPaginated(uriInfo,gameResponseList,games));
//        return response.build();
//    }
//
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response postGame(@Valid @BeanParam SubmitGameForm submitGameForm, BindingResult bindingResult) throws IOException {
//        if(bindingResult.hasErrors()){
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        //TODO:Try catch???
//        Game game = gs.createGame(submitGameForm.toSubmitDTO(),id);//TODO: id de usuario que lo crea
//        //Que hago aca con el suggest, que devuelvo???
//        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(game.getId())).build()).build();
//    }
//
//    @PUT
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response putGame(@Valid @BeanParam SubmitGameForm submitGameForm, BindingResult bindingResult) throws IOException {
//        if (bindingResult.hasErrors()) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        //TODO:Try catch???
//        Game game = gs.editGame(submitGameForm.toSubmitDTO(), id);//TODO: id de usuario que lo crea
//
//        return Response.ok(uriInfo.getAbsolutePathBuilder().path(String.valueOf(game.getId())).build()).build();
//    }

    /*
        TODO:
            - GET /games/{id:\\d+}
            - DELETE /games/{id:\\d+}
            - GET /games{?page, pagesize, search, sort, direction, favouriteFor, recommendedFor, genres, minRating, maxRating, suggestion=false}
            - PUT /games/{id:\\d+}
            - PATCH /games/{id:\\d+} → para marcar suggestion=false al aceptar un juego sugerido. Hay que limitarlo a que no se puedan cambiar otras cosas, aparte del suggestion? (por ejemplo que title=algo tire bad request o algo asi)
            - POST /games
     */


//    @RequestMapping("/game/{id:\\d+}")
//    public ModelAndView gameDetails(
//            @PathVariable("id") final Long gameId,
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pagesize", defaultValue = "8") Integer pageSize,
//            @RequestParam(value = "created", required = false) Boolean created
//    ){
//        final ModelAndView mav =  new ModelAndView("games/game-details");
//        mav.addObject("created", created != null && created);
//        Optional<Game> game = gs.getGameById(gameId);
//        User loggedUser = AuthenticationHelper.getLoggedUser(us);
//        if(pageSize == null || pageSize < 1) {
//            pageSize = DEFAULT_REVIEW_PAGE_SIZE;
//        }
//        if(game.isPresent()){
//            mav.addObject("game",game.get());
//            GameReviewData reviewData = gs.getGameReviewDataByGameId(gameId);
//
//            if(loggedUser!=null) {
//                rs.getReviewOfUserForGame(loggedUser.getId(), gameId).ifPresent((r) -> mav.addObject("myReview", r));
//            }
//            Paginated<Review> otherReviews = rs.getReviewsFromGame(Page.with(page, pageSize), gameId, loggedUser != null ? loggedUser.getId() : null, true);
//            PaginationHelper.paginate(mav,otherReviews);
//            List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
//            queriesToKeepAtPageChange.add(new Pair<>("pagesize", pageSize));
//            mav.addObject("queriesToKeepAtPageChange", QueryHelper.toQueryString(queriesToKeepAtPageChange));
//
//            mav.addObject("gameReviewData", reviewData);
//            mav.addObject("reviews", otherReviews.getList());
//            mav.addObject("discoveryQueue",false);
//            mav.addObject("queryString", "?" );
//        }else{
//            throw new ObjectNotFoundException();
//        }
//        return mav;
//    }
//
//
//    @RequestMapping(value = "/game/list", method = RequestMethod.GET)
//    public ModelAndView gameList(
//            @RequestParam(value = "page", defaultValue = "1") Integer page,
//            @RequestParam(value = "pageSize", required = false) Integer pageSize,
//            @RequestParam(value = "o-crit", defaultValue = "0") Integer orderCriteria,
//            @RequestParam(value = "o-dir", defaultValue = "0") Integer orderDirection,
//            @RequestParam(value = "f-gen", defaultValue = "") List<Integer> genresFilter,
//            @RequestParam(value = "f-rat", defaultValue = "") String ratingFilter,
//            @RequestParam(value = "f-enr", defaultValue = "") Boolean excludeNoRatingFilter,
//            @RequestParam(value = "search", defaultValue = "") String search,
//            @RequestParam(value = "created", required = false) Boolean created,
//            @RequestParam(value = "deleted", required = false) Boolean deleted
//    ){
//        final ModelAndView mav = new ModelAndView("games/game-list");
//
//        User loggedUser = AuthenticationHelper.getLoggedUser(us);
//        List<Genre> allGenres = Arrays.asList(Genre.values());
//
//        if(pageSize == null || pageSize < 1) {
//            pageSize = PAGE_SIZE;
//        }
//
//        float minRating = 1f;
//        float maxRating = 10f;
//        try {
//            String [] ratingFilterArray = ratingFilter.split("t");
//            minRating = Float.parseFloat(ratingFilterArray[0]);
//            maxRating = Float.parseFloat(ratingFilterArray[1]);
//        } catch (Exception ignored) {}
//
//        GameFilter searchFilter = new GameFilterBuilder()
//                .withGameContent(search)
//                .withSuggestion(false)
//                .withGameGenres(genresFilter)
//                .withRatingRange(minRating,maxRating, excludeNoRatingFilter == null || !excludeNoRatingFilter)
//                .build();
//
//        Paginated<Game> games = gs.searchGames(
//                Page.with(page != null ? page: INITIAL_PAGE, pageSize),
//                searchFilter,
//                new Ordering<>(OrderDirection.fromValue(orderDirection), GameOrderCriteria.fromValue(orderCriteria))
//        );
//
//        PaginationHelper.paginate(mav,games);
//
//        mav.addObject("created", created != null && created);
//        mav.addObject("deleted", deleted!= null && deleted);
//        mav.addObject("games", games.getList());
//        mav.addObject("currentPage", page);
//        mav.addObject("orderCriteria", GameOrderCriteria.values());
//        mav.addObject("orderDirections", OrderDirection.values());
//        mav.addObject("searchField",search);
//        mav.addObject("genresFilter", new FilteredList<Genre>(allGenres, (genre) -> genresFilter.contains(genre.getId())));
//        mav.addObject("selectedOrderDirection", OrderDirection.fromValue(orderDirection));
//        mav.addObject("selectedOrderCriteria", GameOrderCriteria.fromValue(orderCriteria));
//        mav.addObject("minRating", minRating);
//        mav.addObject("maxRating", maxRating);
//        mav.addObject("excludeNoRating", excludeNoRatingFilter);
//        if(orderCriteria == 0 && orderDirection == 0 && genresFilter.isEmpty() && search.isEmpty() && ratingFilter.isEmpty() && excludeNoRatingFilter == null){
//            mav.addObject("showResetFiltersButton", false);
//        } else {
//            mav.addObject("showResetFiltersButton", true);
//        }
//
//        List<Pair<String, Object>> queriesToKeepAtPageChange = new ArrayList<>();
//        queriesToKeepAtPageChange.add(Pair.of("o-crit", orderCriteria));
//        queriesToKeepAtPageChange.add(Pair.of("o-dir", orderDirection));
//        queriesToKeepAtPageChange.add(Pair.of("search", search));
//        queriesToKeepAtPageChange.add(Pair.of("f-rat", ratingFilter));
//        if(excludeNoRatingFilter!=null)
//            queriesToKeepAtPageChange.add(Pair.of("f-enr", excludeNoRatingFilter));
//        queriesToKeepAtPageChange.addAll(genresFilter.stream().map((value) -> Pair.of("f-gen", (Object)value)).collect(Collectors.toList()));
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
//    @RequestMapping(value = "/game/delete/{gameId:\\d+}", method = RequestMethod.POST)
//    public ModelAndView deleteGame(@PathVariable(value = "gameId") Long gameId) {
//        try {
//            gs.deleteGame(gameId);
//        } catch (GameNotFoundException e) {
//            return new ModelAndView("redirect:/game/list");
//        }
//        return new ModelAndView("redirect:/game/list" + "?deleted=true");
//    }
//
//    @RequestMapping(value = "/game/{gameId:\\d+}/edit")
//    public ModelAndView editGameForm(@PathVariable("gameId") final Long gameId, @ModelAttribute("gameForm") SubmitGameForm form) {
//        // mejor hacerlo de otra forma
//        Optional<Game> gameOptional = gs.getGameById(gameId);
//        ModelAndView mav = new ModelAndView("games/submit-game");
//        if(gameOptional.isPresent()){
//            Game game = gameOptional.get();
//            form.setName(game.getName());
//            form.setDescription(game.getDescription());
//            form.setDeveloper(game.getDeveloper());
//            form.setPublisher(game.getPublisher());
//            form.setGenres(game.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
//            form.setReleaseDate(game.getPublishDate().format(DateTimeFormatter.ISO_DATE));
//        }
//        mav.addObject("genres", Genre.values());
//        mav.addObject("edit", true);
//        mav.addObject("maxDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
//        return mav;
//    }
//
//    @RequestMapping(value = "/game/{gameId:\\d+}/edit", method = RequestMethod.POST)
//    public ModelAndView editGame(@PathVariable("gameId") final Long gameId,@Valid() @ModelAttribute("gameForm") SubmitGameForm form, final BindingResult errors) {
//        if(errors.hasErrors())
//            return editGameForm(gameId, form);
//        try {
//            gs.editGame(form.toSubmitDTO(), gameId);
//        } catch (IOException e) {
//            LOGGER.error("Failed to create image: {}", e.getMessage());
//            errors.addError(new ObjectError("image", "game.submit.errors.failedimg"));
//        }
//        return new ModelAndView("redirect:/game/" + gameId);
//    }
//
//    @RequestMapping(value = "/game/submit", method = RequestMethod.GET)
//    public ModelAndView createGameForm(@ModelAttribute("gameForm") SubmitGameForm gameForm) {
//        ModelAndView mav = new ModelAndView("games/submit-game");
//        mav.addObject("genres", Genre.values());
//        mav.addObject("edit", false);
//        mav.addObject("maxDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
//        return mav;
//    }
//
//    @RequestMapping(value = "/game/submit", method = RequestMethod.POST)
//    public ModelAndView createGame(@Valid() @ModelAttribute("gameForm") SubmitGameForm gameForm, final BindingResult errors) {
//        if (errors.hasErrors()) {
//            return createGameForm(gameForm);
//        }
//        try {
//            Optional<Game> game = gs.createGame(gameForm.toSubmitDTO(), AuthenticationHelper.getLoggedUser(us).getId());
//            return game.map(value -> new ModelAndView("redirect:/game/" + value.getId() + "?created=true")).orElseGet(() -> new ModelAndView("redirect:/game/list" + "?created=true"));
//        } catch (IOException e) {
//            LOGGER.error("Failed to create image: {}", e.getMessage());
//            errors.addError(new ObjectError("image", "game.submit.errors.failedimg"));
//        } catch (RuntimeException e) {
//            errors.addError(new ObjectError("image", "game.submit.errors.unknown"));
//            return createGameForm(gameForm);
//        }
//        return createGameForm(gameForm);
//    }
//
//    @RequestMapping(value = "/game/submissions/{gameId:\\d+}/accept", method = RequestMethod.POST)
//    public ModelAndView acceptSubmission(@PathVariable(value="gameId") Long gameId) {
//        LOGGER.info("Accepting suggested game, id: {}", gameId);
//        User user = AuthenticationHelper.getLoggedUser(us);
//        gs.acceptGame(gameId, user.getId());
//        return new ModelAndView("redirect:/game/submissions");
//    }
//
//    @RequestMapping(value = "/game/submissions/{gameId:\\d+}/reject", method = RequestMethod.POST)
//    public ModelAndView rejectSubmission(@PathVariable(value="gameId") Long gameId) {
//        LOGGER.info("Rejecting suggested game, id: {}", gameId);
//        User user = AuthenticationHelper.getLoggedUser(us);
//        gs.rejectGame(gameId, user.getId());
//        return new ModelAndView("redirect:/game/submissions");
//    }
//
//    @RequestMapping(value = "/game/submissions", method = RequestMethod.GET)
//    public ModelAndView checkSubmissions(@RequestParam(value = "modalSearch", defaultValue = "") String search) {
//        ModelAndView mav = new ModelAndView("/games/game-addition");
//
//        GameFilter filter = new GameFilterBuilder()
//                .withSuggestion(true)
//                .build();
//        Paginated<Game> suggestedgames = gs.searchGames(
//                Page.with(INITIAL_PAGE, PAGE_SIZE),
//                filter,
//                new Ordering<>(OrderDirection.fromValue(0), GameOrderCriteria.fromValue(1))
//        );
//        PaginationHelper.paginate(mav,suggestedgames);
//        mav.addObject("suggestedgames", suggestedgames.getList());
//
//
//        if(!search.isEmpty()) {
//            GameFilter searchFilter = new GameFilterBuilder()
//                    .withSuggestion(false)
//                    .withGameContent(search)
//                    .build();
//            Paginated<Game> searchedgames = gs.searchGames(
//                    Page.with(INITIAL_PAGE, SMALL_LIST_PAGE_SIZE),
//                    searchFilter,
//                    new Ordering<>(OrderDirection.fromValue(0), GameOrderCriteria.fromValue(1))
//            );
//            mav.addObject("searchedgames", searchedgames.getList());
//        }
//
//        mav.addObject("modalSearch", search);
//        return mav;
//    }
}
