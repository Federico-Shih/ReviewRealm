package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import com.fasterxml.jackson.core.JsonParseException;
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
public class JsonExceptionMapper implements ExceptionMapper<JsonParseException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(JsonParseException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(), messageSource.getMessage("json.parse.exception",
                null, Locale.ENGLISH));
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("json.parse.exception",
                null, LocaleHelper.getLocale()))).build();
    }
}