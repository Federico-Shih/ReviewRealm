package ar.edu.itba.paw.webapp.controller.responses;

import ar.edu.itba.paw.enums.Genre;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.context.MessageSource;

import javax.ws.rs.core.UriInfo;

public class GenreResponse extends BaseResponse {
    private static final String BASE_PATH = "/genres";
    private final int id;
    private final String name;
    private final String localized;

    public GenreResponse(int id, String name, String localized) {
        this.id = id;
        this.name = name;
        this.localized = localized;
    }

    public static GenreResponse fromEntity(final UriInfo uri, Genre genre, String localized) {
        GenreResponse response = new GenreResponse(genre.getId(), genre.name(), localized);
        response.link("self", uri.getBaseUriBuilder().path(BASE_PATH).path(String.valueOf(genre.getId())).build());
        return response;
    }

    public String getLocalized() {
        return localized;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
