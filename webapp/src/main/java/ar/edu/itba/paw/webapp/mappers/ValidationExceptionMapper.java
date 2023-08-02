package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.controller.responses.ValidationErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<ValidationErrorResponse> errors = e.getConstraintViolations().stream()
                .map(ValidationErrorResponse::fromValidationException)
                .collect(Collectors.toList());
        return Response.status(Response.Status.BAD_REQUEST).entity(new GenericEntity<List<ValidationErrorResponse>>(errors){}).build();
    }
}
