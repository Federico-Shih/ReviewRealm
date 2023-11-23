package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.models.Game;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.servicesinterfaces.GameService;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.servicesinterfaces.UserService;
import ar.edu.itba.paw.webapp.auth.AuthenticationHelper;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.responses.GenreResponse;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("genres")
@Component
public class GenreController {
    private static final int cacheMaxAge = 86400;
    private final GenreService genreService;

    private final GameService gameService;

    private final UserService userService;

    @Context
    private UriInfo uriInfo;

    private final MessageSource messageSource;

    @Autowired
    public GenreController(GenreService genreService, MessageSource messageSource,GameService gameService, UserService userService) {
        this.genreService = genreService;
        this.messageSource = messageSource;
        this.gameService = gameService;
        this.userService = userService;
    }

    @GET()
    @Path("{id:\\d+}")
    @Produces(VndType.APPLICATION_GENRE)
    public Response getById(@PathParam("id") Integer id, @Context Request request) {
        return genreService.getGenreById(id)
                .map(genre -> {
                    CacheControl cacheControl = CacheHelper.buildCacheControl(cacheMaxAge);
                    return CacheHelper
                            .buildEtagCache(request, genre, cacheControl,
                                    entity -> Response.ok(GenreResponse.fromEntity(uriInfo, genre, getLocalizedGenre(genre)))
                            );
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)).build();
    }

    @GET()
    @Produces(VndType.APPLICATION_GENRE_LIST)
    public Response getGenres(@Context Request request,
                           @QueryParam("forGame") Long gameId,
                           @QueryParam("forUser") Long userId) {
        if (gameId != null && userId != null) {
            throw new CustomRuntimeException(Response.Status.BAD_REQUEST,"genres.invalid.filter" );
        }
        User loggedUser = AuthenticationHelper.getLoggedUser(userService);
        if(gameId != null){
            Optional<Game> game = gameService.getGameById(gameId, loggedUser != null? loggedUser.getId() : null);
            if(!game.isPresent()){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity(
                    new GenericEntity<List<GenreResponse>>(
                            game.get().getGenres().stream()
                                    .map((genre) -> GenreResponse.fromEntity(uriInfo, genre, getLocalizedGenre(genre)))
                                    .collect(Collectors.toList())
                    ){}
            ).build();
        }
        if(userId != null){
            Optional<User> user = userService.getUserById(userId);
            if(!user.isPresent()){
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok().entity(
                    new GenericEntity<List<GenreResponse>>(
                            user.get().getPreferences().stream()
                                    .map((genre) -> GenreResponse.fromEntity(uriInfo, genre, getLocalizedGenre(genre)))
                                    .collect(Collectors.toList())
                    ){}
            ).build();
        }
        List<Genre> genres = genreService.getGenres();
        CacheControl cacheControl = CacheHelper.buildCacheControl(cacheMaxAge);

        return CacheHelper.buildEtagCache(request, genres, cacheControl,
                entity -> Response.ok().entity(
                        new GenericEntity<List<GenreResponse>>(
                                genres.stream()
                                        .map((genre) -> GenreResponse.fromEntity(uriInfo, genre, getLocalizedGenre(genre)))
                                        .collect(Collectors.toList())
                        ){}
                )).build();
    }

    private String getLocalizedGenre(Genre genre) {
        return messageSource.getMessage(genre.getName(), null, LocaleHelper.getLocale());
    }
}
