package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.Paginated;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;

import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.annotations.ExistentGameId;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.forms.EditGameForm;
import ar.edu.itba.paw.webapp.controller.forms.PatchGameForm;
import ar.edu.itba.paw.webapp.controller.forms.SubmitGameForm;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.querycontainers.GameSearchQuery;
import ar.edu.itba.paw.webapp.controller.responses.GameResponse;
import ar.edu.itba.paw.webapp.controller.responses.PaginatedResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("games")
@Component
public class GameController {

    private final GameService gs;
    private final UserService us;

    @Context
    private UriInfo uriInfo;


    @Autowired
    public GameController(GameService gs, UserService us) {
        this.gs = gs;
        this.us = us;
    }

    @GET
    @Produces(VndType.APPLICATION_GAME)
    @Path("{id:\\d+}")
    public Response getById(@PathParam("id") final long gameId, @Context Request request) {
        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        Optional<Game> game = gs.getGameById(gameId,loggedUser != null ? loggedUser.getId() : null);
        if (game.isPresent()) {
            return Response.ok(
                GameResponse.fromEntity(
                    uriInfo,
                    game.get(),
                    gs.getGameReviewDataByGameId(gameId),
                    loggedUser)
            ).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @GET
    @Produces(VndType.APPLICATION_GAME_LIST)
    @PreAuthorize("@accessControl.checkSuggestedFilterIsModerator(#gameSearchQuery.getSuggested())")
    public Response getGames(@Valid @BeanParam GameSearchQuery gameSearchQuery){
        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        Paginated<Game> games;
        try {
            games = gs.searchGames(gameSearchQuery.getPage(), gameSearchQuery.getFilter(), gameSearchQuery.getOrdering(), (loggedUser != null ? loggedUser.getId() : null));
        }catch (UserNotFoundException e){
            return Response.noContent().build();
        }
        if(games.getTotalPages() == 0 || games.getList().isEmpty()){
            return Response.noContent().build();
        }
        List<GameResponse> gameResponseList = games.getList().stream().map(
                (game) -> GameResponse.fromEntity(uriInfo,game, gs.getGameReviewDataByGameId(game.getId())
                        ,loggedUser)
        ).collect(Collectors.toList());
        return PaginatedResponseHelper.fromPaginated(uriInfo,gameResponseList,games).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postGame(@Valid @NotNull(message = "error.body.empty") @BeanParam SubmitGameForm submitGameForm){
        User loggedUser = AuthenticationHelper.getLoggedUser(us);
        Game game = gs.createGame(submitGameForm.toSubmitDTO(),loggedUser.getId());

        if(!loggedUser.isModerator()){
            return Response.accepted().build();
        }
        return Response
                .created(uriInfo.getAbsolutePathBuilder().path("/games").path(game.getId().toString()).build())
                .entity(GameResponse.fromEntity(uriInfo, game,
                        gs.getGameReviewDataByGameId(game.getId()), loggedUser)).build();
    }

    @PATCH
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(VndType.APPLICATION_GAME)
    @Path("{id:\\d+}")
    public Response  editGame(@Valid @NotNull(message = "error.body.empty") @BeanParam EditGameForm editGameForm,@PathParam("id") final long id){
        Game game = gs.editGame(editGameForm.toSubmitDTO(),id);
        User user = AuthenticationHelper.getLoggedUser(us);

        return Response.ok(uriInfo.getAbsolutePathBuilder().path("/games").path(game.getId().toString()).build())
                .entity(GameResponse.fromEntity(uriInfo, game,
                        gs.getGameReviewDataByGameId(game.getId()), user)).build();
    }

    @DELETE
    @Path("{id:\\d+}")
    public Response deleteGame(@PathParam("id") final long id) {
        boolean deleted = gs.deleteGame(id);
        if(deleted) {
            return Response.ok().build();
        }else{
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Consumes(VndType.APPLICATION_GAME_SUGGESTION_FORM)
    @Path("{id:\\d+}")
    public Response patchGame(@Valid @NotNull(message = "error.body.empty") PatchGameForm patchGameForm,
                              @PathParam("id") final long id) {
        long loggedUser = AuthenticationHelper.getLoggedUser(us).getId();
        if(patchGameForm.getAccept()){
            gs.acceptGame(id, loggedUser);
        }else{
            gs.rejectGame(id,loggedUser);
        }
        return Response.noContent().build();
    }
}
