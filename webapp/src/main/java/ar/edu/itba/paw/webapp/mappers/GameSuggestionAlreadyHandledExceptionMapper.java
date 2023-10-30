package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.GameSuggestionAlreadyHandled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class GameSuggestionAlreadyHandledExceptionMapper implements ExceptionMapper<GameSuggestionAlreadyHandled> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(GameSuggestionAlreadyHandled gameSuggestionAlreadyHandled) {
        return Response.status(Response.Status.BAD_REQUEST).entity(messageSource.getMessage("game.suggestion.already.handled", null, null)).build();
    }
}
