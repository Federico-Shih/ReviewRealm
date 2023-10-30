package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserNotAModeratorException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import ar.edu.itba.paw.webapp.exceptions.CustomRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class UserNotAModeratorExceptionMapper implements ExceptionMapper<UserNotAModeratorException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserNotAModeratorException e) {
        return Response.status(Response.Status.FORBIDDEN).entity(messageSource.getMessage("user.not.moderator", null,
                LocaleHelper.getLocale())).build();
    }
}
