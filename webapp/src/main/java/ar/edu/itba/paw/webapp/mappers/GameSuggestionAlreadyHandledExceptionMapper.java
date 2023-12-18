package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.GameSuggestionAlreadyHandled;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Component
@Provider
public class GameSuggestionAlreadyHandledExceptionMapper implements ExceptionMapper<GameSuggestionAlreadyHandled> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSuggestionAlreadyHandledExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(GameSuggestionAlreadyHandled gameSuggestionAlreadyHandled) {
        LOGGER.error("{}: {}", gameSuggestionAlreadyHandled.getClass().getName(), messageSource.getMessage("game.suggestion.already.handled",
                null, Locale.ENGLISH));
        return Response.status(Response.Status.CONFLICT).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("game.suggestion.already.handled",
                null, LocaleHelper.getLocale()))).build();
    }
}
