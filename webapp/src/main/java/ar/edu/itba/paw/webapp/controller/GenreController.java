package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.servicesinterfaces.GenreService;
import ar.edu.itba.paw.webapp.controller.cache.CacheHelper;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.mediatypes.VndType;
import ar.edu.itba.paw.webapp.controller.responses.GenreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Path("/genres")
@Component
public class GenreController {
    private static final int cacheMaxAge = 86400;
    private final GenreService genreService;

    @Context
    private UriInfo uriInfo;

    private final MessageSource messageSource;

    @Autowired
    public GenreController(GenreService genreService, MessageSource messageSource) {
        this.genreService = genreService;
        this.messageSource = messageSource;
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
    @Path("/")
    @Produces(VndType.APPLICATION_GENRE_LIST)
    public Response getAll(@Context Request request) {
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
