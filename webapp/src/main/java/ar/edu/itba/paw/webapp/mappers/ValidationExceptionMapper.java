package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ValidationErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Autowired
    private MessageSource messageSource;
    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ValidationErrorResponse> errors = e.getConstraintViolations().stream()
                .map(ValidationErrorResponse::fromValidationException)
                .peek(error -> error.setMessage(messageSource.getMessage(error.getMessage(), new Object[] {error.getProperty()}, LocaleHelper.getLocale())))
                .collect(Collectors.toList());
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(new GenericEntity<List<ValidationErrorResponse>>(errors){}).build();
    }
}
