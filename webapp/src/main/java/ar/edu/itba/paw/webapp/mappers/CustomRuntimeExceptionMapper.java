package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
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
public class CustomRuntimeExceptionMapper implements ExceptionMapper<CustomRuntimeException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomRuntimeExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(CustomRuntimeException e) {
        LOGGER.error("Runtime exception: {}", messageSource.getMessage(e.getMessage(), null, Locale.ENGLISH));
        return Response.status(e.getCode()).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(messageSource.getMessage(e.getMessage(), null, LocaleHelper.getLocale()))).build();
    }
}
