package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserAlreadyEnabled;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.controller.responses.ExceptionResponse;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class UserAlreadyEnabledExceptionMapper implements ExceptionMapper<UserAlreadyEnabled> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserAlreadyEnabled userAlreadyEnabled) {
        return Response.status(Response.Status.BAD_REQUEST).entity(ExceptionResponse.of(messageSource.getMessage("user.already.validated", null,
                LocaleHelper.getLocale()))).build();
    }
}
