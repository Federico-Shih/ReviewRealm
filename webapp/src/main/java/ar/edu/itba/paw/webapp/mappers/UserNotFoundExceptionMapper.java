package ar.edu.itba.paw.webapp.mappers;


import ar.edu.itba.paw.exceptions.UserNotFoundException;
import ar.edu.itba.paw.webapp.controller.helpers.LocaleHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserNotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(messageSource.getMessage("user.not.found", null,
                LocaleHelper.getLocale())).build();
    }
}
