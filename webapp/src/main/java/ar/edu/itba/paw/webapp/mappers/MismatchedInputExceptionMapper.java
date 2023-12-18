package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

@Provider
@Component
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MismatchedInputExceptionMapper.class);
    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(MismatchedInputException e) {
        LOGGER.error("{}: {}", e.getClass().getName(), messageSource.getMessage("mismatch.input.exception",
                null, Locale.ENGLISH));
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(ExceptionResponse.of(
                messageSource.getMessage("mismatch.input.exception", null,
                        LocaleHelper.getLocale()))).build();
    }
}
