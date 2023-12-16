package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserNotAModeratorException;
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
public class UserNotAModeratorExceptionMapper implements ExceptionMapper<UserNotAModeratorException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserNotAModeratorExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserNotAModeratorException e) {
        LOGGER.error("{}: {}", e.getClass().getName(), messageSource.getMessage("user.not.moderator",
                null, Locale.ENGLISH));
        return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("user.not.moderator", null,
                LocaleHelper.getLocale()))).build();
    }
}
