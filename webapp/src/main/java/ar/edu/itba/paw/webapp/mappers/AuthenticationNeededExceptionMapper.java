package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.AuthenticationNeededException;
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
public class AuthenticationNeededExceptionMapper implements ExceptionMapper<AuthenticationNeededException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationNeededExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

     @Override
     public Response toResponse(AuthenticationNeededException e) {
            LOGGER.error("{}: {}", e.getClass().getName(),messageSource.getMessage("authentication.needed", null, Locale.ENGLISH));
         return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("authentication.needed", null,
                 LocaleHelper.getLocale()))).build();
     }

}
