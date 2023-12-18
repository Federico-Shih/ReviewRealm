package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class BadCredentialsExceptionMapper implements ExceptionMapper<BadCredentialsException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BadCredentialsExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(BadCredentialsException e) {
        System.out.println(e.getMessage());
        LOGGER.error("{}: {}", e.getClass().getName(), e.getMessage());
        return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage("unauthorized", null, LocaleHelper.getLocale()))).build();
    }
}
