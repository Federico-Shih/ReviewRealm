package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.GameNotFoundException;
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
public class GameNotFoundExceptionMapper implements ExceptionMapper<GameNotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(GameNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("game.not.found", null,
                LocaleHelper.getLocale()))).build();
    }
}
