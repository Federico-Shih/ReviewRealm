package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.GenreNotFoundException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class GenreNotFoundExceptionMapper implements ExceptionMapper<GenreNotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(GenreNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("genre.not.found",
                new Object[]{e.getGenreId()}, LocaleHelper.getLocale()))).build();
    }
}
