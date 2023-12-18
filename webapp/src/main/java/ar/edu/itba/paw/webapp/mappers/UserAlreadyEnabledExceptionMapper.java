package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
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
public class UserAlreadyEnabledExceptionMapper implements ExceptionMapper<UserAlreadyEnabled> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAlreadyEnabledExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserAlreadyEnabled userAlreadyEnabled) {
        LOGGER.error("{}: {}", userAlreadyEnabled.getClass().getName(), messageSource.getMessage("user.already.validated", null,
                Locale.ENGLISH));
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("user.already.validated", null,
                LocaleHelper.getLocale()))).build();
    }
}
