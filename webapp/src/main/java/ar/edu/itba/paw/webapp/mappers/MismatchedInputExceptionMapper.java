package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Component
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(MismatchedInputException e) {
        return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionResponse.of(
                messageSource.getMessage("mismatch.input.exception", null,
                        LocaleHelper.getLocale()))).build();
    }
}
